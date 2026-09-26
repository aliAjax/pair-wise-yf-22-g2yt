package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.constants.ActorRole;
import com.generated.qualityTrace.middlewares.AuthMiddleware;
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.routes.DefectRecordRoutes;
import com.generated.qualityTrace.services.DefectRecordService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(DefectRecordRoutes.PATH)
public class DefectRecordController {
  private final DefectRecordService service;
  private final RbacMiddleware rbac;

  public DefectRecordController(DefectRecordService service, RbacMiddleware rbac) {
    this.service = service;
    this.rbac = rbac;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list();
  }

  /** 质检员/质量经理关闭不良记录 */
  @PostMapping(DefectRecordRoutes.CLOSE)
  public Map<String, Object> close(@PathVariable long id, HttpServletRequest request) {
    rbac.requireRole(request, ActorRole.QUALITY_INSPECTOR, ActorRole.QUALITY_MANAGER);
    String actor = (String) request.getAttribute(AuthMiddleware.ATTR_ACTOR);
    return service.close(id, actor);
  }
}
