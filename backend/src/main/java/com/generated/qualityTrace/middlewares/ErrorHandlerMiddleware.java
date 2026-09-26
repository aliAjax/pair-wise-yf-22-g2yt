package com.generated.qualityTrace.middlewares;

import com.generated.qualityTrace.constants.ErrorCodes;
import com.generated.qualityTrace.constants.ErrorMessages;
import com.generated.qualityTrace.exceptions.ApiException;
import java.util.LinkedHashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** 全局异常处理：ApiException 原样输出 code/message/details，其余兜底 500 */
@RestControllerAdvice
public class ErrorHandlerMiddleware {
  private static final Logger log = LoggerFactory.getLogger(ErrorHandlerMiddleware.class);

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<Map<String, Object>> handleApi(ApiException e) {
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", e.getCode());
    body.put("message", e.getMessage());
    if (e.getDetails() != null) {
      body.put("details", e.getDetails());
    }
    return ResponseEntity.status(e.getStatus()).body(body);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<Map<String, Object>> handleOther(Exception e) {
    log.error("unhandled error", e);
    Map<String, Object> body = new LinkedHashMap<>();
    body.put("code", ErrorCodes.INTERNAL_ERROR);
    body.put("message", ErrorMessages.INTERNAL_ERROR);
    return ResponseEntity.status(500).body(body);
  }
}
