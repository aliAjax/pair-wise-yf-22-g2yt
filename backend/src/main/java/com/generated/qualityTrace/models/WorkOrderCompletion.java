package com.generated.qualityTrace.models;

import java.util.List;

/** 工单完工收尾记录：成功后落库，同一工单重发时取回第一次结果 */
public class WorkOrderCompletion {
  public Long id;
  public Long workOrderId;
  /** 取值见 constants/CompletionResult */
  public String result;
  public String submittedBy;
  public String submittedAt;
  public List<BatchCompletionSummary> batches;

  public WorkOrderCompletion() {}

  public WorkOrderCompletion(Long id, Long workOrderId, String result, String submittedBy,
                             String submittedAt, List<BatchCompletionSummary> batches) {
    this.id = id;
    this.workOrderId = workOrderId;
    this.result = result;
    this.submittedBy = submittedBy;
    this.submittedAt = submittedAt;
    this.batches = batches;
  }
}
