package com.generated.qualityTrace.middlewares;

import java.io.IOException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import com.generated.qualityTrace.constants.Role;

/** 从 X-Role 请求头解析角色，写入请求属性供 RbacMiddleware 校验；不拦截无角色的读请求。 */
@Component
public class AuthMiddleware extends OncePerRequestFilter {

  public static final String ROLE_ATTRIBUTE = "qualityTrace.role";
  public static final String ROLE_HEADER = "X-Role";

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws ServletException, IOException {
    String header = request.getHeader(ROLE_HEADER);
    if (header != null && !header.isBlank()) {
      try {
        request.setAttribute(ROLE_ATTRIBUTE, Role.valueOf(header.trim().toUpperCase()));
      } catch (IllegalArgumentException ignored) {
        // 未知角色按未认证处理，由 RbacMiddleware 在写接口上拒绝
      }
    }
    chain.doFilter(request, response);
  }
}
