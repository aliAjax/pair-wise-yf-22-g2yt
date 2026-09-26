package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ActorRole;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ApiException;
import jakarta.servlet.http.HttpServletRequest;
import java.util.Arrays;
import org.springframework.stereotype.Component;

/** RBAC：controller 在写操作前调用 requireRole，异常由 ErrorHandlerMiddleware 序列化 */
@Component
public class RbacMiddleware {

  public void requireRole(HttpServletRequest request, ActorRole... allowed) {
    Object role = request.getAttribute(AuthMiddleware.ATTR_ROLE);
    boolean ok = role != null && Arrays.stream(allowed).anyMatch(r -> r.name().equals(role));
    if (!ok) {
      throw new ApiException(403, ErrorCodes.RBAC_DENIED, ErrorMessages.RBAC_DENIED);
    }
  }
}
