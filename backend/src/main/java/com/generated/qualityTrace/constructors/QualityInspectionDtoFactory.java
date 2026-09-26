package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.QualityInspection;
import java.util.LinkedHashMap;
import java.util.Map;

public final class QualityInspectionDtoFactory {
  private QualityInspectionDtoFactory() {}

  public static Map<String, Object> toDto(QualityInspection inspection) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", inspection.id);
    map.put("batchId", inspection.batchId);
    map.put("inspectorId", inspection.inspectorId);
    map.put("inspectionType", inspection.inspectionType);
    map.put("standardVersion", inspection.standardVersion);
    map.put("resultStatus", inspection.resultStatus);
    map.put("inspectedAt", inspection.inspectedAt);
    return map;
  }
}
