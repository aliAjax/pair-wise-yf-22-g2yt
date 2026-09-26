package com.generated.qualityTrace.constants;

public final class ErrorMessages {
  public static final String AUTH_REQUIRED = "缺少角色身份信息（请携带 X-Role 请求头）";
  public static final String RBAC_DENIED = "当前角色无权执行该操作";
  public static final String WORK_ORDER_NOT_FOUND = "工单不存在";
  public static final String WORK_ORDER_NO_BATCHES = "工单下没有任何批次，不能提交完工收尾";
  public static final String CLOSEOUT_INVALID_STATE = "当前工单状态不允许提交完工收尾: ";
  public static final String DEFECT_NOT_FOUND = "不良记录不存在";
  public static final String DEFECT_ALREADY_CLOSED = "不良记录已关闭，请勿重复操作";
  public static final String INTERNAL_ERROR = "服务内部错误";

  private ErrorMessages() {}
}
