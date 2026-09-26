package com.generated.qualityTrace.constructors;

import com.generated.qualityTrace.models.ProductBatch;
import java.util.LinkedHashMap;
import java.util.Map;

public final class ProductBatchDtoFactory {
  private ProductBatchDtoFactory() {}

  public static Map<String, Object> toDto(ProductBatch batch) {
    Map<String, Object> map = new LinkedHashMap<>();
    map.put("id", batch.id);
    map.put("batchNo", batch.batchNo);
    map.put("workOrderId", batch.workOrderId);
    map.put("quantity", batch.quantity);
    map.put("materialLotNo", batch.materialLotNo);
    map.put("producedAt", batch.producedAt);
    map.put("batchStatus", batch.batchStatus);
    return map;
  }
}
