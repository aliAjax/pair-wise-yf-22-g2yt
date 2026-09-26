package com.generated.qualityTrace.models;

import java.util.ArrayList;
import java.util.List;

/** 单个批次的完工汇总：末检结论 + 未关闭不良 + 阻塞原因 */
public class BatchCompletionSummary {
  public Long batchId;
  public String batchNo;
  /** 末检（FINAL）结论，取 constants/InspectionResultStatus；无末检记录时为 null */
  public String finalInspectionResult;
  public int openDefectCount;
  public int openCriticalDefectCount;
  /** 取值见 constants/CompletionBlockReason */
  public List<String> blockReasons = new ArrayList<>();

  public BatchCompletionSummary() {}

  public BatchCompletionSummary(Long batchId, String batchNo, String finalInspectionResult,
                                int openDefectCount, int openCriticalDefectCount,
                                List<String> blockReasons) {
    this.batchId = batchId;
    this.batchNo = batchNo;
    this.finalInspectionResult = finalInspectionResult;
    this.openDefectCount = openDefectCount;
    this.openCriticalDefectCount = openCriticalDefectCount;
    this.blockReasons = blockReasons;
  }
}
