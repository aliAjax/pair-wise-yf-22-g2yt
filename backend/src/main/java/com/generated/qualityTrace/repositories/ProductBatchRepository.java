package com.generated.qualityTrace.repositories;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.models.ProductBatch;

@Repository
public class ProductBatchRepository {

  private final ConcurrentHashMap<Long, ProductBatch> store = new ConcurrentHashMap<>();

  public ProductBatchRepository() {
    save(new ProductBatch(1L, "B-1001A", 1L, 200, "M-6601", "2026-09-20T10:00:00+08:00", "DONE"));
    save(new ProductBatch(2L, "B-1001B", 1L, 300, "M-6602", "2026-09-20T15:00:00+08:00", "DONE"));
    save(new ProductBatch(3L, "B-1002A", 2L, 400, "M-6603", "2026-09-21T10:00:00+08:00", "DONE"));
    save(new ProductBatch(4L, "B-1002B", 2L, 400, "M-6604", "2026-09-21T16:00:00+08:00", "DONE"));
    save(new ProductBatch(5L, "B-1003A", 3L, 300, "M-6605", "2026-09-22T11:00:00+08:00", "HOLD"));
  }

  public List<ProductBatch> findAll() {
    return store.values().stream().sorted(Comparator.comparing(b -> b.id)).toList();
  }

  public Optional<ProductBatch> findById(long id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<ProductBatch> findByWorkOrderId(long workOrderId) {
    return store.values().stream()
        .filter(b -> b.workOrderId == workOrderId)
        .sorted(Comparator.comparing(b -> b.id))
        .toList();
  }

  public ProductBatch save(ProductBatch batch) {
    store.put(batch.id, batch);
    return batch;
  }
}
