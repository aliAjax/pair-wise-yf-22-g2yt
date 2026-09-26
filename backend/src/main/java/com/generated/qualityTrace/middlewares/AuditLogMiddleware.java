package com.generated.qualityTrace.middlewares;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.generated.qualityTrace.constants.LogTemplates;

/** 写操作审计日志：记录方法、路径和角色。 */
@Component
public class AuditLogMiddleware extends OncePerRequestFilter {

  private static final Logger log = LoggerFactory.getLogger(AuditLogMiddleware.class);

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    if (!"GET".equalsIgnoreCase(request.getMethod())) {
      Object role = request.getAttribute(AuthMiddleware.ROLE_ATTRIBUTE);
      log.info(LogTemplates.AUDIT_WRITE, request.getMethod(), request.getRequestURI(),
          role == null ? "anonymous" : role.toString());
    }
    chain.doFilter(request, response);
  }
}
