package com.generated.qualityTrace.types;

import java.util.List;

/**
 * 单个批次的收尾汇总：末检结论 + 未关闭不良数量 + 阻塞原因。
 * blockReasons 取值见 constants/CloseoutBlockReason。
 */
public record BatchCloseoutSummary(
    Long batchId,
    String batchNo,
    String batchStatus,
    String finalConclusion,
    long openDefectCount,
    long openCriticalDefectCount,
    long totalDefectCount,
    boolean blocked,
    List<String> blockReasons
) {}
