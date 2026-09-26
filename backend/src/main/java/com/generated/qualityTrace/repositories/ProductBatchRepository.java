package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.ProductBatch;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class ProductBatchRepository {
  private final List<ProductBatch> store = new CopyOnWriteArrayList<>();

  public ProductBatchRepository() {
    store.add(new ProductBatch(101L, "B-1001-A", 1L, 500, "M-7701", "2026-09-18T10:00:00Z", "PRODUCED"));
    store.add(new ProductBatch(102L, "B-1001-B", 1L, 500, "M-7702", "2026-09-18T15:00:00Z", "PRODUCED"));
    store.add(new ProductBatch(201L, "B-1002-A", 2L, 250, "M-8801", "2026-09-19T09:00:00Z", "PRODUCED"));
    store.add(new ProductBatch(202L, "B-1002-B", 2L, 250, "M-8802", "2026-09-19T14:00:00Z", "PRODUCED"));
    store.add(new ProductBatch(301L, "B-1003-A", 3L, 800, "M-9901", "2026-09-20T08:00:00Z", "PRODUCED"));
  }

  public List<ProductBatch> findAll() {
    return List.copyOf(store);
  }

  public List<ProductBatch> findByWorkOrderId(long workOrderId) {
    return store.stream()
        .filter(b -> b.workOrderId == workOrderId)
        .collect(Collectors.toList());
  }
}
