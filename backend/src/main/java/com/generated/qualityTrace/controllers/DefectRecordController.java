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
import com.generated.qualityTrace.middlewares.RbacMiddleware;
import com.generated.qualityTrace.routes.DefectRecordRoutes;
import com.generated.qualityTrace.services.DefectRecordService;

@RestController
@RequestMapping(DefectRecordRoutes.PATH)
public class DefectRecordController {

  private final DefectRecordService service;

  public DefectRecordController(DefectRecordService service) {
    this.service = service;
  }

  @GetMapping
  public List<Map<String, Object>> list() {
    return service.list();
  }

  /** 质检员/质量经理关闭不良；最后一条严重不良关闭后，工单收尾可再次提交。 */
  @PostMapping(DefectRecordRoutes.CLOSE)
  public Map<String, Object> close(@PathVariable long id, HttpServletRequest request) {
    Role actor = RbacMiddleware.requireAny(request, Role.INSPECTOR, Role.QUALITY_MANAGER);
    return service.close(id, actor);
  }
}
