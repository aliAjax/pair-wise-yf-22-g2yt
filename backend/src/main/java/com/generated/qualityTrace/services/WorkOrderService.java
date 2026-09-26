package com.generated.qualityTrace.services;

import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constructors.WorkOrderDtoFactory;
import com.generated.qualityTrace.models.WorkOrder;
import com.generated.qualityTrace.models.WorkOrderCloseout;
import com.generated.qualityTrace.repositories.WorkOrderCloseoutRepository;
import com.generated.qualityTrace.repositories.WorkOrderRepository;
import com.generated.qualityTrace.types.BatchCloseoutSummary;
import com.generated.qualityTrace.utils.ApiException;

@Service
public class WorkOrderService {

  private final WorkOrderRepository repo;
  private final WorkOrderCloseoutRepository closeoutRepo;
  private final WorkOrderCloseoutService closeoutService;

  public WorkOrderService(WorkOrderRepository repo,
                          WorkOrderCloseoutRepository closeoutRepo,
                          WorkOrderCloseoutService closeoutService) {
    this.repo = repo;
    this.closeoutRepo = closeoutRepo;
    this.closeoutService = closeoutService;
  }

  /** 原有清单接口，照常可用。 */
  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(WorkOrderDtoFactory::listItem).toList();
  }

  /** 工单详情：各批次末检结论、不良数量和阻塞原因，以及最近一次收尾结果。 */
  public Map<String, Object> detail(long id) {
    WorkOrder order = repo.findById(id)
        .orElseThrow(() -> new ApiException(404, ErrorCodes.WORK_ORDER_NOT_FOUND, ErrorMessages.WORK_ORDER_NOT_FOUND));
    List<BatchCloseoutSummary> batches = closeoutService.summarizeBatches(order);
    WorkOrderCloseout latest = closeoutRepo.findLatestByWorkOrderId(id).orElse(null);
    return WorkOrderDtoFactory.detail(order, batches, latest);
  }
}
