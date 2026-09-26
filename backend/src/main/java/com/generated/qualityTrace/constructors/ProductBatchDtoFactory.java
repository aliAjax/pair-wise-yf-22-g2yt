package com.generated.qualityTrace.constructors;

import java.util.LinkedHashMap;
import java.util.Map;
import com.generated.qualityTrace.models.ProductBatch;

public final class ProductBatchDtoFactory {

  public static Map<String, Object> toDto(ProductBatch b) {
    Map<String, Object> dto = new LinkedHashMap<>();
    dto.put("id", b.id);
    dto.put("batchNo", b.batchNo);
    dto.put("workOrderId", b.workOrderId);
    dto.put("quantity", b.quantity);
    dto.put("materialLotNo", b.materialLotNo);
    dto.put("producedAt", b.producedAt);
    dto.put("batchStatus", b.batchStatus);
    return dto;
  }

  private ProductBatchDtoFactory() {}
}
