package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.models.WorkOrderCloseout;
import com.generated.qualityTrace.types.BatchCloseoutSummary;
import com.generated.qualityTrace.types.WorkOrderCloseoutPayload;

public final class WorkOrderCloseoutDtoFactory {

  /** 批次收尾汇总行：末检结论 + 不良数量 + 阻塞原因。 */
  public static Map<String, Object> batchDto(BatchCloseoutSummary s) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("batchId", s.batchId());
    dto.put("batchNo", s.batchNo());
    dto.put("batchStatus", s.batchStatus());
    dto.put("finalConclusion", s.finalConclusion());
    dto.put("openDefectCount", s.openDefectCount());
    dto.put("openCriticalDefectCount", s.openCriticalDefectCount());
    dto.put("totalDefectCount", s.totalDefectCount());
    dto.put("blocked", s.blocked());
    dto.put("blockReasons", s.blockReasons());
    return dto;
  }

  /** 收尾记录快照（工单详情内嵌）。 */
  public static Map<String, Object> recordDto(WorkOrderCloseout record) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("closeoutId", record.id);
    dto.put("outcome", record.outcome);
    dto.put("attemptNo", record.attemptNo);
    dto.put("createdAt", record.createdAt);
    dto.put("released", record.released);
    dto.put("blockedBatchNos", record.blockedBatches().stream().map(BatchCloseoutSummary::batchNo).toList());
    return dto;
  }

  /** 提交收尾的响应：结论、工单状态、批次汇总、阻塞批次；重发时 idempotentReplay=true。 */
  public static Map<String, Object> resultDto(WorkOrderCloseoutPayload payload) {
    WorkOrderCloseout record = payload.record();
    WorkOrder order = payload.order();
    List<BatchCloseoutSummary> blocked = record.blockedBatches();

    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("closeoutId", record.id);
    dto.put("workOrderId", order.id);
    dto.put("orderNo", order.orderNo);
    dto.put("outcome", record.outcome);
    dto.put("orderStatus", order.status);
    dto.put("attemptNo", record.attemptNo);
    dto.put("idempotentReplay", payload.idempotentReplay());
    dto.put("batches", record.batches.stream().map(WorkOrderCloseoutDtoFactory::batchDto).toList());
    dto.put("blockedBatches", blocked.stream().map(WorkOrderCloseoutDtoFactory::batchDto).toList());
    dto.put("createdAt", record.createdAt);
    return dto;
  }

  private WorkOrderCloseoutDtoFactory() {}
}
