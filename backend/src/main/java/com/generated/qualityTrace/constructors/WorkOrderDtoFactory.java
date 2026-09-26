package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.models.WorkOrderCloseout;
import com.generated.qualityTrace.types.BatchCloseoutSummary;
import com.generated.qualityTrace.utils.Formatters;

public final class WorkOrderDtoFactory {

  /** 清单行：保持原有 GET /api/work-order 清单可用的字段结构。 */
  public static Map<String, Object> listItem(WorkOrder order) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", order.id);
    dto.put("orderNo", order.orderNo);
    dto.put("productCode", order.productCode);
    dto.put("productName", order.productName);
    dto.put("plannedQty", order.plannedQty);
    dto.put("lineCode", order.lineCode);
    dto.put("startAt", order.startAt);
    dto.put("status", order.status);
    dto.put("statusText", Formatters.workOrderStatusText(order.status));
    return dto;
  }

  /**
   * 工单详情：各批次末检结论、未关闭不良数量、阻塞原因 + 最近一次收尾结果。
   * 质量员通过该接口查看批次结论和未关闭不良。
   */
  public static Map<String, Object> detail(WorkOrder order, List<BatchCloseoutSummary> batches,
                                           WorkOrderCloseout latestCloseout) {
    Map<String, Object> dto = listItem(order);
    dto.put("batches", batches.stream().map(WorkOrderCloseoutDtoFactory::batchDto).toList());
    dto.put("blockedBatchNos", batches.stream().filter(BatchCloseoutSummary::blocked).map(BatchCloseoutSummary::batchNo).toList());
    dto.put("closeout", latestCloseout == null ? null : WorkOrderCloseoutDtoFactory.recordDto(latestCloseout));
    return dto;
  }

  private WorkOrderDtoFactory() {}
}
