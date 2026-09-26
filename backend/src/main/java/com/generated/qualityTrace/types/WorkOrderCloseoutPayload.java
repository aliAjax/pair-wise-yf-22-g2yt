package com.generated.qualityTrace.types;

import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.models.WorkOrderCloseout;

/**
 * 完工收尾提交结果：service 产出，controller 交给 DTO 构造器渲染。
 * idempotentReplay=true 表示同一工单重发，取回的是第一次的收尾结果。
 */
public record WorkOrderCloseoutPayload(
    WorkOrderCloseout record,
    WorkOrder order,
    boolean idempotentReplay
) {}
