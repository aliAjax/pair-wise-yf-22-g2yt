package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.DefectRecord;
import java.util.LinkedHashMap;
import java.util.Map;

public final class DefectRecordDtoFactory {
  private DefectRecordDtoFactory() {}

  public static Map<String, Object> toDto(DefectRecord defect) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", defect.id);
    map.put("batchId", defect.batchId);
    map.put("defectType", defect.defectType);
    map.put("defectQty", defect.defectQty);
    map.put("severity", defect.severity);
    map.put("rootCause", defect.rootCause);
    map.put("dispositionStatus", defect.dispositionStatus);
    return map;
  }
}
