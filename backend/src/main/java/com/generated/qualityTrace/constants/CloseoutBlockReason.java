package com.generated.qualityTrace.constants;

/** 完工收尾阻塞原因码，按批次挂在收尾结果和工单详情上。 */
public enum CloseoutBlockReason {
  /** 批次还没有末检记录 */
  FINAL_INSPECTION_MISSING,
  /** 末检结论不是 PASS / CONDITIONAL_PASS */
  FINAL_INSPECTION_NOT_PASSED,
  /** 批次仍带未关闭的严重不良（CRITICAL） */
  OPEN_CRITICAL_DEFECT
}
