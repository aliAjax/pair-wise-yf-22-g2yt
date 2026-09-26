package com.generated.qualityTrace.middlewares;

import jakarta.servlet.http.HttpServletRequest;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.Role;
import com.generated.qualityTrace.utils.ApiException;

/** 角色校验：写接口在 controller 入口调用，未认证 401、越权 403。 */
public final class RbacMiddleware {

  public static Role requireAny(HttpServletRequest request, Role... allowed) {
    Object attr = request.getAttribute(AuthMiddleware.ROLE_ATTRIBUTE);
    if (!(attr instanceof Role role)) {
      throw new ApiException(401, ErrorCodes.AUTH_REQUIRED, ErrorMessages.AUTH_REQUIRED);
    }
    for (Role candidate : allowed) {
      if (candidate == role) {
        return role;
      }
    }
    throw new ApiException(403, ErrorCodes.RBAC_DENIED, ErrorMessages.RBAC_DENIED);
  }

  private RbacMiddleware() {}
}
