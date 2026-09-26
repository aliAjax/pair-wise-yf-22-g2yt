package com.generated.qualityTrace.models;

public class DefectRecord {
  public Long id;
  public Long batchId;
  public String defectType;
  public Integer defectQty;
  /** 取值见 constants/DefectSeverity：MINOR / MAJOR / CRITICAL */
  public String severity;
  public String rootCause;
  /** 取值见 constants/DefectDispositionStatus：OPEN / PROCESSING / CLOSED */
  public String dispositionStatus;

  public DefectRecord() {}

  public DefectRecord(Long id, Long batchId, String defectType, Integer defectQty,
                      String severity, String rootCause, String dispositionStatus) {
    this.id = id;
    this.batchId = batchId;
    this.defectType = defectType;
    this.defectQty = defectQty;
    this.severity = severity;
    this.rootCause = rootCause;
    this.dispositionStatus = dispositionStatus;
  }
}
