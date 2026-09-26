package com.generated.qualityTrace.constants;

public final class LogTemplates {
  public static final String CREATE = "create";
  public static final String UPDATE = "update";
  public static final String STATUS = "status";
  public static final String EXPORT = "export";

  /** 完工收尾：提交 / 幂等重放 / 阻塞 / 放行 / 释放 */
  public static final String CLOSEOUT_SUBMIT = "[closeout] submit workOrderId={} actor={}";
  public static final String CLOSEOUT_REPLAY = "[closeout] idempotent replay workOrderId={} closeoutId={} outcome={}";
  public static final String CLOSEOUT_BLOCKED = "[closeout] blocked workOrderId={} blockedBatches={}";
  public static final String CLOSEOUT_APPROVED = "[closeout] approved workOrderId={} attemptNo={}";
  public static final String CLOSEOUT_RELEASED = "[closeout] released workOrderId={} closeoutId={} reason=last critical defect closed";

  /** 不良记录：关闭 */
  public static final String DEFECT_CLOSED = "[defect] closed defectId={} batchId={} severity={} actor={}";

  /** 审计：写操作 */
  public static final String AUDIT_WRITE = "[audit] {} {} role={}";

  private LogTemplates() {}
}
