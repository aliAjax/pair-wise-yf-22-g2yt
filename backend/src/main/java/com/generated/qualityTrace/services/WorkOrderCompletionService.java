package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.CompletionBlockReason;
import com.generated.qualityTrace.constants.CompletionResult;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.exceptions.ApiException;
import com.generated.qualityTrace.models.BatchCompletionSummary;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.models.QualityInspection;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.models.WorkOrderCompletion;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import com.generated.qualityTrace.repositories.WorkOrderCompletionRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.utils.Formatters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/** 完工收尾：按批次汇总末检结论与未关闭不良，决定工单完工还是回到暂停 */
@Service
public class WorkOrderCompletionService {
  private static final Logger log = LoggerFactory.getLogger(WorkOrderCompletionService.class);

  private final WorkOrderRepository workOrderRepo;
  private final ProductBatchRepository batchRepo;
  private final QualityInspectionRepository inspectionRepo;
  private final DefectRecordRepository defectRepo;
  private final WorkOrderCompletionRepository completionRepo;

  public WorkOrderCompletionService(WorkOrderRepository workOrderRepo,
                                    ProductBatchRepository batchRepo,
                                    QualityInspectionRepository inspectionRepo,
                                    DefectRecordRepository defectRepo,
                                    WorkOrderCompletionRepository completionRepo) {
    this.workOrderRepo = workOrderRepo;
    this.batchRepo = batchRepo;
    this.inspectionRepo = inspectionRepo;
    this.defectRepo = defectRepo;
    this.completionRepo = completionRepo;
  }

  /** 产线主管提交完工收尾；同一工单重发取回第一次结果 */
  public Map<String, Object> submit(long workOrderId, String actor) {
    WorkOrder order = workOrderRepo.findById(workOrderId)
        .orElseThrow(() -> new ApiException(404, ErrorCodes.WORK_ORDER_NOT_FOUND,
            ErrorMessages.WORK_ORDER_NOT_FOUND));

    Optional<WorkOrderCompletion> existing = completionRepo.findByWorkOrderId(workOrderId);
    if (existing.isPresent()) {
      log.info(String.format(LogTemplates.COMPLETION_REPLAY, workOrderId, actor));
      return WorkOrderDtoFactory.toCompletionResponse(existing.get(), order, true);
    }

    if (WorkOrderStatus.CANCELLED.name().equals(order.status)
        || WorkOrderStatus.FINISHED.name().equals(order.status)) {
      throw new ApiException(409, ErrorCodes.WORK_ORDER_NOT_COMPLETABLE,
          ErrorMessages.WORK_ORDER_NOT_COMPLETABLE);
    }

    log.info(String.format(LogTemplates.COMPLETION_SUBMIT, workOrderId, actor));
    List<BatchCompletionSummary> summaries = summarize(workOrderId);
    List<Long> blocking = WorkOrderDtoFactory.blockingBatchIds(summaries);

    if (!blocking.isEmpty()) {
      order.status = WorkOrderStatus.PAUSED.name();
      workOrderRepo.save(order);
      log.info(String.format(LogTemplates.COMPLETION_BLOCKED, workOrderId, blocking));
      return WorkOrderDtoFactory.toCompletionResponse(order, CompletionResult.BLOCKED.name(),
          summaries, actor, Formatters.nowIso(), false);
    }

    order.status = WorkOrderStatus.FINISHED.name();
    workOrderRepo.save(order);
    WorkOrderCompletion record = completionRepo.save(new WorkOrderCompletion(null, workOrderId,
        CompletionResult.FINISHED.name(), actor, Formatters.nowIso(), summaries));
    log.info(String.format(LogTemplates.COMPLETION_FINISHED, workOrderId, actor));
    return WorkOrderDtoFactory.toCompletionResponse(record, order, false);
  }

  /** 按批次汇总：末检结论 + 未关闭不良数量 + 阻塞原因 */
  public List<BatchCompletionSummary> summarize(long workOrderId) {
    List<BatchCompletionSummary> summaries = new ArrayList<>();
    for (ProductBatch batch : batchRepo.findByWorkOrderId(workOrderId)) {
      String finalResult = inspectionRepo.findLatestFinalByBatchId(batch.id)
          .map((QualityInspection i) -> i.resultStatus)
          .orElse(null);
      List<DefectRecord> open = defectRepo.findOpenByBatchId(batch.id);
      long openCritical = open.stream()
          .filter(d -> DefectSeverity.CRITICAL.name().equals(d.severity))
          .count();

      List<String> reasons = new ArrayList<>();
      if (InspectionResultStatus.FAIL.name().equals(finalResult)) {
        reasons.add(CompletionBlockReason.FINAL_INSPECTION_FAILED.name());
      }
      if (openCritical > 0) {
        reasons.add(CompletionBlockReason.OPEN_CRITICAL_DEFECTS.name());
      }
      summaries.add(new BatchCompletionSummary(batch.id, batch.batchNo, finalResult,
          open.size(), (int) openCritical, reasons));
    }
    return summaries;
  }

  public Optional<WorkOrderCompletion> findCompletion(long workOrderId) {
    return completionRepo.findByWorkOrderId(workOrderId);
  }
}
