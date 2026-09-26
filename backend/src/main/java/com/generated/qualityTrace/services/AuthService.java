package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constants.ActorRole;
import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.constants.LogTemplates;
import com.generated.qualityTrace.exceptions.ApiException;
import com.generated.qualityTrace.utils.JwtUtils;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/** 本地开发登录：种子用户换取 JWT，角色用于 RBAC */
@Service
public class AuthService {
  private static final Logger log = LoggerFactory.getLogger(AuthService.class);
  private static final long TOKEN_TTL_SECONDS = 12 * 3600;

  private static final Map<String, ActorRole> USERS = Map.of(
      "inspector1", ActorRole.QUALITY_INSPECTOR,
      "supervisor1", ActorRole.LINE_SUPERVISOR,
      "manager1", ActorRole.QUALITY_MANAGER,
      "auditor1", ActorRole.AUDITOR);

  private final String secret;

  public AuthService(@Value("${JWT_SECRET:local-dev-secret}") String secret) {
    this.secret = secret;
  }

  public Map<String, Object> login(String username) {
    ActorRole role = USERS.get(username);
    if (role == null) {
      throw new ApiException(401, ErrorCodes.LOGIN_FAILED, ErrorMessages.LOGIN_FAILED);
    }
    log.info(String.format(LogTemplates.LOGIN, username, role.name()));
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("token", JwtUtils.issue(username, role.name(), secret, TOKEN_TTL_SECONDS));
    body.put("username", username);
    body.put("role", role.name());
    body.put("expiresIn", TOKEN_TTL_SECONDS);
    return body;
  }
}
