package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.BatchCompletionSummary;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.models.WorkOrderCompletion;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/** 工单相关响应构造：清单行、详情（含批次结论/不良数/阻塞原因）、完工收尾结果 */
public final class WorkOrderDtoFactory {
  private WorkOrderDtoFactory() {}

  public static Map<String, Object> toListItem(WorkOrder order) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", order.id);
    map.put("orderNo", order.orderNo);
    map.put("productCode", order.productCode);
    map.put("productName", order.productName);
    map.put("plannedQty", order.plannedQty);
    map.put("lineCode", order.lineCode);
    map.put("startAt", order.startAt);
    map.put("status", order.status);
    return map;
  }

  public static Map<String, Object> toBatchSummary(BatchCompletionSummary summary) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("batchId", summary.batchId);
    map.put("batchNo", summary.batchNo);
    map.put("finalInspectionResult", summary.finalInspectionResult);
    map.put("openDefectCount", summary.openDefectCount);
    map.put("openCriticalDefectCount", summary.openCriticalDefectCount);
    map.put("blockReasons", summary.blockReasons);
    return map;
  }

  public static List<Long> blockingBatchIds(List<BatchCompletionSummary> summaries) {
    return summaries.stream()
        .filter(s -> !s.blockReasons.isEmpty())
        .map(s -> s.batchId)
        .toList();
  }

  public static Map<String, Object> toDetail(WorkOrder order, List<BatchCompletionSummary> summaries,
                                             WorkOrderCompletion lastCompletion) {
    Map<String, Object> map = toListItem(order);
    List<Long> blocking = blockingBatchIds(summaries);
    map.put("blocked", !blocking.isEmpty());
    map.put("batches", summaries.stream().map(WorkOrderDtoFactory::toBatchSummary).toList());
    map.put("blockingBatches", blocking);
    map.put("lastCompletion", lastCompletion == null ? null : toCompletionRecord(lastCompletion));
    return map;
  }

  /** 完工提交响应：FINISHED / BLOCKED 同一形状；重发时 replayed=true 且内容取自第一次结果 */
  public static Map<String, Object> toCompletionResponse(WorkOrder order, String result,
                                                         List<BatchCompletionSummary> summaries,
                                                         String submittedBy, String submittedAt,
                                                         boolean replayed) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("workOrderId", order.id);
    map.put("orderNo", order.orderNo);
    map.put("result", result);
    map.put("status", order.status);
    map.put("replayed", replayed);
    map.put("batches", summaries.stream().map(WorkOrderDtoFactory::toBatchSummary).toList());
    map.put("blockingBatches", blockingBatchIds(summaries));
    map.put("submittedBy", submittedBy);
    map.put("submittedAt", submittedAt);
    return map;
  }

  public static Map<String, Object> toCompletionResponse(WorkOrderCompletion completion, WorkOrder order,
                                                         boolean replayed) {
    return toCompletionResponse(order, completion.result, completion.batches,
        completion.submittedBy, completion.submittedAt, replayed);
  }

  private static Map<String, Object> toCompletionRecord(WorkOrderCompletion completion) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("result", completion.result);
    map.put("submittedBy", completion.submittedBy);
    map.put("submittedAt", completion.submittedAt);
    return map;
  }
}
