package com.generated.qualityTrace.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.CloseoutBlockReason;
import com.generated.qualityTrace.constants.CloseoutOutcome;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.Role;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.models.WorkOrderCloseout;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.repositories.WorkOrderCloseoutRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.BatchCloseoutSummary;
import com.generated.qualityTrace.types.WorkOrderCloseoutPayload;
import com.generated.qualityTrace.utils.ApiException;
import com.generated.qualityTrace.utils.Formatters;
import com.generated.qualityTrace.validators.WorkOrderCloseoutValidator;

/**
 * 完工收尾：按批次汇总末检结论和未关闭不良。
 * 有批次未通过末检或带未关闭严重不良 → 工单回到 PAUSED 并记录阻塞批次；
 * 全部通过 → 工单 FINISHED。同一工单重发取回第一次结果，
 * 最后一条严重不良关闭后记录被释放，可再次提交。
 */
@Service
public class WorkOrderCloseoutService {

  private static final Logger log = LoggerFactory.getLogger(WorkOrderCloseoutService.class);

  private final WorkOrderRepository workOrderRepo;
  private final ProductBatchRepository batchRepo;
  private final QualityInspectionRepository inspectionRepo;
  private final DefectRecordRepository defectRepo;
  private final WorkOrderCloseoutRepository closeoutRepo;
  private final WorkOrderCloseoutValidator validator;

  public WorkOrderCloseoutService(WorkOrderRepository workOrderRepo,
                                  ProductBatchRepository batchRepo,
                                  QualityInspectionRepository inspectionRepo,
                                  DefectRecordRepository defectRepo,
                                  WorkOrderCloseoutRepository closeoutRepo,
                                  WorkOrderCloseoutValidator validator) {
    this.workOrderRepo = workOrderRepo;
    this.batchRepo = batchRepo;
    this.inspectionRepo = inspectionRepo;
    this.defectRepo = defectRepo;
    this.closeoutRepo = closeoutRepo;
    this.validator = validator;
  }

  /** 产线主管提交完工收尾。 */
  public WorkOrderCloseoutPayload submit(long workOrderId, Role actor) {
    log.info(LogTemplates.CLOSEOUT_SUBMIT, workOrderId, actor);
    WorkOrder order = workOrderRepo.findById(workOrderId)
        .orElseThrow(() -> new ApiException(404, ErrorCodes.WORK_ORDER_NOT_FOUND, ErrorMessages.WORK_ORDER_NOT_FOUND));

    // 同一工单重发：取回第一次的收尾结果，不重复评估、不重复改状态
    Optional<WorkOrderCloseout> active = closeoutRepo.findActiveByWorkOrderId(workOrderId);
    if (active.isPresent()) {
      log.info(LogTemplates.CLOSEOUT_REPLAY, workOrderId, active.get().id, active.get().outcome);
      return new WorkOrderCloseoutPayload(active.get(), order, true);
    }

    validator.validateSubmittable(order);
    List<BatchCloseoutSummary> summaries = summarizeBatches(order);
    boolean anyBlocked = summaries.stream().anyMatch(BatchCloseoutSummary::blocked);

    String outcome;
    if (anyBlocked) {
      order.status = WorkOrderStatus.PAUSED.name();
      outcome = CloseoutOutcome.BLOCKED.name();
    } else {
      order.status = WorkOrderStatus.FINISHED.name();
      outcome = CloseoutOutcome.APPROVED.name();
    }
    workOrderRepo.save(order);

    WorkOrderCloseout record = closeoutRepo.save(new WorkOrderCloseout(
        null, workOrderId, outcome, 0, summaries, Formatters.now(), false));

    if (anyBlocked) {
      log.info(LogTemplates.CLOSEOUT_BLOCKED, workOrderId,
          record.blockedBatches().stream().map(BatchCloseoutSummary::batchNo).toList());
    } else {
      log.info(LogTemplates.CLOSEOUT_APPROVED, workOrderId, record.attemptNo);
    }
    return new WorkOrderCloseoutPayload(record, order, false);
  }

  /** 按批次实时汇总：末检结论、未关闭不良数量、阻塞原因（工单详情与收尾评估共用）。 */
  public List<BatchCloseoutSummary> summarizeBatches(WorkOrder order) {
    List<ProductBatch> batches = batchRepo.findByWorkOrderId(order.id);
    if (batches.isEmpty()) {
      throw new ApiException(400, ErrorCodes.WORK_ORDER_NO_BATCHES, ErrorMessages.WORK_ORDER_NO_BATCHES);
    }
    List<BatchCloseoutSummary> summaries = new ArrayList<>();
    for (ProductBatch batch : batches) {
      summaries.add(summarizeBatch(batch));
    }
    return summaries;
  }

  private BatchCloseoutSummary summarizeBatch(ProductBatch batch) {
    Optional<QualityInspection> finalInspection = inspectionRepo.findLatestFinalByBatchId(batch.id);
    String conclusion = finalInspection.map(i -> i.resultStatus).orElse(null);

    List<DefectRecord> defects = defectRepo.findByBatchId(batch.id);
    long openCount = defects.stream().filter(WorkOrderCloseoutService::isOpen).count();
    long openCritical = defects.stream()
        .filter(WorkOrderCloseoutService::isOpen)
        .filter(d -> DefectSeverity.CRITICAL.name().equals(d.severity))
        .count();

    List<String> reasons = new ArrayList<>();
    if (finalInspection.isEmpty()) {
      reasons.add(CloseoutBlockReason.FINAL_INSPECTION_MISSING.name());
    } else if (!passed(conclusion)) {
      reasons.add(CloseoutBlockReason.FINAL_INSPECTION_NOT_PASSED.name());
    }
    if (openCritical > 0) {
      reasons.add(CloseoutBlockReason.OPEN_CRITICAL_DEFECT.name());
    }
    return new BatchCloseoutSummary(batch.id, batch.batchNo, batch.batchStatus, conclusion,
        openCount, openCritical, defects.size(), !reasons.isEmpty(), List.copyOf(reasons));
  }

  /** 末检通过口径：PASS 或 CONDITIONAL_PASS 视为通过。 */
  private static boolean passed(String conclusion) {
    return InspectionResultStatus.PASS.name().equals(conclusion)
        || InspectionResultStatus.CONDITIONAL_PASS.name().equals(conclusion);
  }

  private static boolean isOpen(DefectRecord d) {
    return !DefectDispositionStatus.CLOSED.name().equals(d.dispositionStatus);
  }
}
