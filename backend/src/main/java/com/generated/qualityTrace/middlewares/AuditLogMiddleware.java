package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.LogTemplates;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/** 操作日志：每个 /api 请求结束后记录一条审计行 */
@Component
public class AuditLogMiddleware implements HandlerInterceptor {
  private static final Logger log = LoggerFactory.getLogger(AuditLogMiddleware.class);

  @Override
  public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                              Object handler, Exception ex) {
    Object actor = request.getAttribute(AuthMiddleware.ATTR_ACTOR);
    log.info(String.format(LogTemplates.AUDIT_REQUEST,
        request.getMethod(), request.getRequestURI(),
        actor == null ? "anonymous" : actor, response.getStatus()));
  }
}
