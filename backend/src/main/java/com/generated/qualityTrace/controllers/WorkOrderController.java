package com.generated.qualityTrace.controllers;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.generated.qualityTrace.constants.Role;
import com.generated.qualityTrace.constructors.WorkOrderCloseoutDtoFactory;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.routes.WorkOrderRoutes;
import com.generated.qualityTrace.services.WorkOrderCloseoutService;
import com.generated.qualityTrace.services.WorkOrderService;
import com.generated.qualityTrace.types.WorkOrderCloseoutPayload;

@RestController
@RequestMapping(WorkOrderRoutes.PATH)
public class WorkOrderController {

  private final WorkOrderService service;
  private final WorkOrderCloseoutService closeoutService;

  public WorkOrderController(WorkOrderService service, WorkOrderCloseoutService closeoutService) {
    this.service = service;
    this.closeoutService = closeoutService;
  }

  /** 原有工单清单，照常可用。 */
  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list();
  }

  /** 工单详情：各批次末检结论、不良数量和阻塞原因。 */
  @GetMapping(WorkOrderRoutes.DETAIL)
  public Map<String, Object> detail(@PathVariable long id) {
    return service.detail(id);
  }

  /** 产线主管提交完工收尾；同一工单重发取回第一次结果。 */
  @PostMapping(WorkOrderRoutes.CLOSEOUT)
  public Map<String, Object> closeout(@PathVariable long id, HttpServletRequest request) {
    Role actor = RbacMiddleware.requireAny(request, Role.LINE_SUPERVISOR);
    WorkOrderCloseoutPayload payload = closeoutService.submit(id, actor);
    return WorkOrderCloseoutDtoFactory.resultDto(payload);
  }
}
