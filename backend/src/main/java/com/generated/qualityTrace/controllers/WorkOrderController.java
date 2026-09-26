package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.ActorRole;
import com.generated.qualityTrace.middlewares.AuthMiddleware;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.routes.WorkOrderRoutes;
import com.generated.qualityTrace.services.WorkOrderCompletionService;
import com.generated.qualityTrace.services.WorkOrderService;
import com.generated.qualityTrace.types.CompletionRequestPayload;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(WorkOrderRoutes.PATH)
public class WorkOrderController {
  private final WorkOrderService service;
  private final WorkOrderCompletionService completionService;
  private final RbacMiddleware rbac;

  public WorkOrderController(WorkOrderService service,
                             WorkOrderCompletionService completionService,
                             RbacMiddleware rbac) {
    this.service = service;
    this.completionService = completionService;
    this.rbac = rbac;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list();
  }

  /** 工单详情：各批次末检结论、未关闭不良数量、阻塞原因 */
  @GetMapping(WorkOrderRoutes.DETAIL)
  public Map<String, Object> detail(@PathVariable long id) {
    return service.getDetail(id);
  }

  /** 产线主管提交完工收尾；同一工单重发取回第一次结果 */
  @PostMapping(WorkOrderRoutes.COMPLETION)
  public Map<String, Object> complete(@PathVariable long id,
                                      @RequestBody(required = false) CompletionRequestPayload body,
                                      HttpServletRequest request) {
    rbac.requireRole(request, ActorRole.LINE_SUPERVISOR);
    String actor = (String) request.getAttribute(AuthMiddleware.ATTR_ACTOR);
    return completionService.submit(id, actor);
  }
}
