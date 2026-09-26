package com.generated.qualityTrace.services;

import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constants.Role;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.models.ProductBatch;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import com.generated.qualityTrace.repositories.ProductBatchRepository;
import com.generated.qualityTrace.repositories.WorkOrderCloseoutRepository;
import com.generated.qualityTrace.utils.ApiException;
import com.generated.qualityTrace.validators.DefectCloseValidator;

@Service
public class DefectRecordService {

  private static final Logger log = LoggerFactory.getLogger(DefectRecordService.class);

  private final DefectRecordRepository repo;
  private final ProductBatchRepository batchRepo;
  private final WorkOrderCloseoutRepository closeoutRepo;
  private final DefectCloseValidator validator;

  public DefectRecordService(DefectRecordRepository repo,
                             ProductBatchRepository batchRepo,
                             WorkOrderCloseoutRepository closeoutRepo,
                             DefectCloseValidator validator) {
    this.repo = repo;
    this.batchRepo = batchRepo;
    this.closeoutRepo = closeoutRepo;
    this.validator = validator;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(DefectRecordDtoFactory::toDto).toList();
  }

  /**
   * 关闭不良。若关闭的是工单最后一条未关闭严重不良，
   * 释放被阻塞的收尾记录，产线主管可再次提交完工收尾。
   */
  public Map<String, Object> close(long defectId, Role actor) {
    DefectRecord defect = repo.findById(defectId)
        .orElseThrow(() -> new ApiException(404, ErrorCodes.DEFECT_NOT_FOUND, ErrorMessages.DEFECT_NOT_FOUND));
    validator.validateClosable(defect);

    defect.dispositionStatus = DefectDispositionStatus.CLOSED.name();
    repo.save(defect);
    log.info(LogTemplates.DEFECT_CLOSED, defect.id, defect.batchId, defect.severity, actor);

    boolean released = false;
    if (DefectSeverity.CRITICAL.name().equals(defect.severity)) {
      ProductBatch batch = batchRepo.findById(defect.batchId)
          .orElseThrow(() -> new ApiException(500, ErrorCodes.INTERNAL_ERROR, ErrorMessages.INTERNAL_ERROR));
      List<Long> orderBatchIds = batchRepo.findByWorkOrderId(batch.workOrderId).stream().map(b -> b.id).toList();
      boolean anyOpenCriticalLeft = !repo.findOpenCriticalByBatchIds(orderBatchIds).isEmpty();
      if (!anyOpenCriticalLeft) {
        released = closeoutRepo.releaseBlockedByWorkOrderId(batch.workOrderId).isPresent();
        if (released) {
          log.info(LogTemplates.CLOSEOUT_RELEASED, batch.workOrderId, defect.id);
        }
      }
    }
    return DefectRecordDtoFactory.closedDto(defect, released);
  }
}
