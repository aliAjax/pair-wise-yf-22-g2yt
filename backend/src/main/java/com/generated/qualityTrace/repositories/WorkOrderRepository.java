package com.generated.qualityTrace.repositories;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.WorkOrderStatus;
import com.generated.qualityTrace.models.WorkOrder;

@Repository
public class WorkOrderRepository {

  private final ConcurrentHashMap<Long, WorkOrder> store = new ConcurrentHashMap<>();

  public WorkOrderRepository() {
    save(new WorkOrder(1L, "WO-1001", "P-AX100", "轴承座", 500, "L1", "2026-09-20T08:00:00+08:00", WorkOrderStatus.RUNNING.name()));
    save(new WorkOrder(2L, "WO-1002", "P-AX200", "齿轮", 800, "L1", "2026-09-21T08:00:00+08:00", WorkOrderStatus.RUNNING.name()));
    save(new WorkOrder(3L, "WO-1003", "P-AX300", "法兰盘", 300, "L2", "2026-09-22T08:00:00+08:00", WorkOrderStatus.PAUSED.name()));
    save(new WorkOrder(4L, "WO-1004", "P-AX400", "密封圈", 1000, "L2", "2026-09-25T08:00:00+08:00", WorkOrderStatus.PLANNED.name()));
  }

  public List<WorkOrder> findAll() {
    return store.values().stream().sorted(Comparator.comparing(o -> o.id)).toList();
  }

  public Optional<WorkOrder> findById(long id) {
    return Optional.ofNullable(store.get(id));
  }

  public WorkOrder save(WorkOrder order) {
    store.put(order.id, order);
    return order;
  }
}
