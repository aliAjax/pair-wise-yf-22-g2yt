package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.WorkOrder;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;

@Repository
public class WorkOrderRepository {
  private final List<WorkOrder> store = new CopyOnWriteArrayList<>();

  public WorkOrderRepository() {
    store.add(new WorkOrder(1L, "WO-1001", "P-001", "轴套", 1000, "L1", "2026-09-18", WorkOrderStatus.RUNNING.name()));
    store.add(new WorkOrder(2L, "WO-1002", "P-002", "齿轮", 500, "L2", "2026-09-19", WorkOrderStatus.RUNNING.name()));
    store.add(new WorkOrder(3L, "WO-1003", "P-003", "法兰", 800, "L1", "2026-09-20", WorkOrderStatus.RUNNING.name()));
  }

  public List<WorkOrder> findAll() {
    return List.copyOf(store);
  }

  public Optional<WorkOrder> findById(long id) {
    return store.stream().filter(o -> o.id == id).findFirst();
  }

  public void save(WorkOrder order) {
    for (int i = 0; i < store.size(); i++) {
      if (store.get(i).id.equals(order.id)) {
        store.set(i, order);
        return;
      }
    }
    store.add(order);
  }
}
