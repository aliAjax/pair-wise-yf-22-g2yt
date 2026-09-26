package com.generated.qualityTrace.models;

public class QualityInspection {
  public Long id;
  public Long batchId;
  public String inspectorId;
  /** 取值见 constants/InspectionType：FIRST / PATROL / FINAL */
  public String inspectionType;
  public String standardVersion;
  /** 取值见 constants/InspectionResultStatus */
  public String resultStatus;
  public String inspectedAt;

  public QualityInspection() {}

  public QualityInspection(Long id, Long batchId, String inspectorId, String inspectionType,
                           String standardVersion, String resultStatus, String inspectedAt) {
    this.id = id;
    this.batchId = batchId;
    this.inspectorId = inspectorId;
    this.inspectionType = inspectionType;
    this.standardVersion = standardVersion;
    this.resultStatus = resultStatus;
    this.inspectedAt = inspectedAt;
  }
}
