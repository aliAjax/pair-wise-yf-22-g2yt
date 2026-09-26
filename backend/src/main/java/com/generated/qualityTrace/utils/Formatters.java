package com.generated.qualityTrace.utils;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

public final class Formatters {
  public static String audit(String type, long id) {
    return type + "#" + id;
  }

  /** 统一时间戳格式：完工时间、关闭时间、日志时间均走这里 */
  public static String nowIso() {
    return OffsetDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
  }
}
