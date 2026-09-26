package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.Map;
import com.generated.qualityTrace.models.DefectRecord;

public final class DefectRecordDtoFactory {

  public static Map<String, Object> toDto(DefectRecord d) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", d.id);
    dto.put("batchId", d.batchId);
    dto.put("defectType", d.defectType);
    dto.put("defectQty", d.defectQty);
    dto.put("severity", d.severity);
    dto.put("rootCause", d.rootCause);
    dto.put("dispositionStatus", d.dispositionStatus);
    return dto;
  }

  /** 关闭不良的响应：closeoutReleased=true 表示这是工单最后一条严重不良，收尾已可重新提交。 */
  public static Map<String, Object> closedDto(DefectRecord d, boolean closeoutReleased) {
    Map<String, Object> dto = toDto(d);
    dto.put("closeoutReleased", closeoutReleased);
    return dto;
  }

  private DefectRecordDtoFactory() {}
}
