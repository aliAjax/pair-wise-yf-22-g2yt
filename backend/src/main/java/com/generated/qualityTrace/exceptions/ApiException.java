package com.generated.qualityTrace.exceptions;

/** 业务异常：service 抛出，ErrorHandlerMiddleware 统一序列化为 {code, message, details} */
public class ApiException extends RuntimeException {
  private final int status;
  private final String code;
  private final transient Object details;

  public ApiException(int status, String code, String message) {
    this(status, code, message, null);
  }

  public ApiException(int status, String code, String message, Object details) {
    super(message);
    this.status = status;
    this.code = code;
    this.details = details;
  }

  public int getStatus() {
    return status;
  }

  public String getCode() {
    return code;
  }

  public Object getDetails() {
    return details;
  }
}
