package com.generated.qualityTrace.config;

import com.generated.qualityTrace.middlewares.AuditLogMiddleware;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class AppConfig implements WebMvcConfigurer {
  private final AuditLogMiddleware auditLogMiddleware;

  public AppConfig(AuditLogMiddleware auditLogMiddleware) {
    this.auditLogMiddleware = auditLogMiddleware;
  }

  @Override
  public void addInterceptors(InterceptorRegistry registry) {
    registry.addInterceptor(auditLogMiddleware).addPathPatterns("/api/**");
  }
}
