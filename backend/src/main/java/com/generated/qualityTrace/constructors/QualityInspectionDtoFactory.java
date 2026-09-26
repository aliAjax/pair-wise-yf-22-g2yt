package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.Map;
import com.generated.qualityTrace.models.QualityInspection;

public final class QualityInspectionDtoFactory {

  public static Map<String, Object> toDto(QualityInspection i) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", i.id);
    dto.put("batchId", i.batchId);
    dto.put("inspectorId", i.inspectorId);
    dto.put("inspectionType", i.inspectionType);
    dto.put("standardVersion", i.standardVersion);
    dto.put("resultStatus", i.resultStatus);
    dto.put("inspectedAt", i.inspectedAt);
    return dto;
  }

  private QualityInspectionDtoFactory() {}
}
