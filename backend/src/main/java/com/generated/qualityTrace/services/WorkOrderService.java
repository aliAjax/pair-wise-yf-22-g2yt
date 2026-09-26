package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.exceptions.ApiException;
import com.generated.qualityTrace.models.BatchCompletionSummary;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.models.WorkOrderCompletion;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class WorkOrderService {
  private final WorkOrderRepository repo;
  private final WorkOrderCompletionService completionService;

  public WorkOrderService(WorkOrderRepository repo, WorkOrderCompletionService completionService) {
    this.repo = repo;
    this.completionService = completionService;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(WorkOrderDtoFactory::toListItem).toList();
  }

  /** 工单详情：各批次末检结论、未关闭不良数量、阻塞原因、最近一次完工结果 */
  public Map<String, Object> getDetail(long id) {
    WorkOrder order = repo.findById(id)
        .orElseThrow(() -> new ApiException(404, ErrorCodes.WORK_ORDER_NOT_FOUND,
            ErrorMessages.WORK_ORDER_NOT_FOUND));
    List<BatchCompletionSummary> summaries = completionService.summarize(id);
    WorkOrderCompletion completion = completionService.findCompletion(id).orElse(null);
    return WorkOrderDtoFactory.toDetail(order, summaries, completion);
  }
}
