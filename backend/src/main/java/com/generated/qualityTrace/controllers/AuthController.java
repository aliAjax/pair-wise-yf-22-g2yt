package com.generated.qualityTrace.controllers;

import com.generated.qualityTrace.routes.AuthRoutes;
import com.generated.qualityTrace.services.AuthService;
import com.generated.qualityTrace.types.LoginPayload;
import com.generated.qualityTrace.validators.LoginValidator;
import java.util.Map;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(AuthRoutes.PATH)
public class AuthController {
  private final AuthService service;

  public AuthController(AuthService service) {
    this.service = service;
  }

  @PostMapping(AuthRoutes.LOGIN)
  public Map<String, Object> login(@RequestBody LoginPayload payload) {
    LoginValidator.validate(payload);
    return service.login(payload.username());
  }
}
