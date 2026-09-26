package com.generated.qualityTrace.utils;

import java.time.OffsetDateTime;
import com.generated.qualityTrace.constants.WorkOrderStatus;

public final class Formatters {

  public static String audit(String type, long id) { return type + "#" + id; }

  public static String now() { return OffsetDateTime.now().toString(); }

  /** 工单状态中文文案，详情/清单 DTO 共用。 */
  public static String workOrderStatusText(String status) {
    if (status == null) return "未知";
    return switch (status) {
      case "PLANNED" -> "已计划";
      case "RUNNING" -> "生产中";
      case "PAUSED" -> "已暂停";
      case "FINISHED" -> "已完工";
      case "CANCELLED" -> "已取消";
      default -> "未知";
    };
  }

  static { // 触发 WorkOrderStatus 装载，保持枚举引用链
    WorkOrderStatus.values();
  }

  private Formatters() {}
}
