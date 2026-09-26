package com.generated.qualityTrace.middlewares;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.utils.JwtUtils;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

/** JWT 认证：校验 Bearer token，把 actor / role 写入请求属性供 RBAC 使用 */
@Component
public class AuthMiddleware extends OncePerRequestFilter {
  public static final String ATTR_ACTOR = "actor";
  public static final String ATTR_ROLE = "role";

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private final String secret;

  public AuthMiddleware(@Value("${JWT_SECRET:local-dev-secret}") String secret) {
    this.secret = secret;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    String path = request.getRequestURI();
    return path.equals("/health")
        || path.startsWith("/actuator")
        || path.startsWith("/api/auth/");
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                  FilterChain chain) throws IOException, jakarta.servlet.ServletException {
    String header = request.getHeader("Authorization");
    if (header == null || !header.startsWith("Bearer ")) {
      writeUnauthorized(response);
      return;
    }
    try {
      Map<String, Object> claims = JwtUtils.verify(header.substring(7), secret);
      request.setAttribute(ATTR_ACTOR, String.valueOf(claims.get("sub")));
      request.setAttribute(ATTR_ROLE, String.valueOf(claims.get("role")));
      chain.doFilter(request, response);
    } catch (SecurityException e) {
      writeUnauthorized(response);
    }
  }

  private void writeUnauthorized(HttpServletResponse response) throws IOException {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", ErrorCodes.AUTH_REQUIRED);
    body.put("message", ErrorMessages.AUTH_REQUIRED);
    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
    response.setCharacterEncoding("UTF-8");
    response.getWriter().write(MAPPER.writeValueAsString(body));
  }
}
