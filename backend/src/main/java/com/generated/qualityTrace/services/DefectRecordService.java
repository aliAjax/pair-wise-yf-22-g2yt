package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.constructors.DefectRecordDtoFactory;
import com.generated.qualityTrace.exceptions.ApiException;
import com.generated.qualityTrace.models.DefectRecord;
import com.generated.qualityTrace.repositories.DefectRecordRepository;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class DefectRecordService {
  private static final Logger log = LoggerFactory.getLogger(DefectRecordService.class);

  private final DefectRecordRepository repo;

  public DefectRecordService(DefectRecordRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(DefectRecordDtoFactory::toDto).toList();
  }

  /** 关闭不良：最后一条严重不良关闭后，被阻塞的工单可重新提交完工 */
  public Map<String, Object> close(long id, String actor) {
    DefectRecord defect = repo.findById(id)
        .orElseThrow(() -> new ApiException(404, ErrorCodes.DEFECT_NOT_FOUND,
            ErrorMessages.DEFECT_NOT_FOUND));
    if (DefectDispositionStatus.CLOSED.name().equals(defect.dispositionStatus)) {
      throw new ApiException(409, ErrorCodes.DEFECT_ALREADY_CLOSED,
          ErrorMessages.DEFECT_ALREADY_CLOSED);
    }
    defect.dispositionStatus = DefectDispositionStatus.CLOSED.name();
    repo.save(defect);
    log.info(String.format(LogTemplates.DEFECT_CLOSED, id, actor));
    return DefectRecordDtoFactory.toDto(defect);
  }
}
