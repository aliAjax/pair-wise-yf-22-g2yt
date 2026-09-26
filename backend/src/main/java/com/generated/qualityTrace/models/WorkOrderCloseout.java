package com.generated.qualityTrace.models;

import java.util.List;
import com.generated.qualityTrace.types.BatchCloseoutSummary;

/**
 * 完工收尾记录。同一工单同一时刻最多一条未释放（released=false）的记录：
 * 未释放期间重发收尾直接取回该记录（第一次结果）；
 * 最后一条严重不良关闭后被释放，允许再次提交。
 */
public class WorkOrderCloseout {
  public Long id;
  public Long workOrderId;
  /** 取值见 constants/CloseoutOutcome：APPROVED / BLOCKED */
  public String outcome;
  public int attemptNo;
  /** 提交时刻的批次汇总快照 */
  public List<BatchCloseoutSummary> batches;
  public String createdAt;
  public boolean released;

  public WorkOrderCloseout() {}

  public WorkOrderCloseout(Long id, Long workOrderId, String outcome, int attemptNo,
                           List<BatchCloseoutSummary> batches, String createdAt, boolean released) {
    this.id = id;
    this.workOrderId = workOrderId;
    this.outcome = outcome;
    this.attemptNo = attemptNo;
    this.batches = batches;
    this.createdAt = createdAt;
    this.released = released;
  }

  public List<BatchCloseoutSummary> blockedBatches() {
    return batches == null ? List.of() : batches.stream().filter(BatchCloseoutSummary::blocked).toList();
  }
}
