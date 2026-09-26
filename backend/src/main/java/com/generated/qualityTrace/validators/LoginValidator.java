package com.generated.qualityTrace.validators;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ApiException;
import com.generated.qualityTrace.types.LoginPayload;

public final class LoginValidator {
  private LoginValidator() {}

  public static void validate(LoginPayload payload) {
    if (payload == null || payload.username() == null || payload.username().isBlank()) {
      throw new ApiException(400, ErrorCodes.VALIDATION_FAILED, ErrorMessages.VALIDATION_FAILED);
    }
  }
}
