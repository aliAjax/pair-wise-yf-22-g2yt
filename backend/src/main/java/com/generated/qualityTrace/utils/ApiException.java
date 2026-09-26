package com.generated.qualityTrace.utils;

/** 业务异常：service 抛出，ErrorHandlerMiddleware 统一渲染为 {code, message}。 */
public class ApiException extends RuntimeException {
  public final int status;
  public final String code;

  public ApiException(int status, String code, String message) {
    super(message);
    this.status = status;
    this.code = code;
  }
}
