package com.generated.qualityTrace.repositories;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.CloseoutOutcome;
import com.generated.qualityTrace.models.WorkOrderCloseout;

@Repository
public class WorkOrderCloseoutRepository {

  private final ConcurrentHashMap<Long, WorkOrderCloseout> store = new ConcurrentHashMap<>();
  private final AtomicLong idSeq = new AtomicLong(0);

  /** 当前生效（未释放）的收尾记录：存在期间同一工单重发取回第一次结果。 */
  public Optional<WorkOrderCloseout> findActiveByWorkOrderId(long workOrderId) {
    return store.values().stream()
        .filter(c -> c.workOrderId == workOrderId && !c.released)
        .findFirst();
  }

  /** 最近一次收尾记录（含已释放），工单详情展示用。 */
  public Optional<WorkOrderCloseout> findLatestByWorkOrderId(long workOrderId) {
    return store.values().stream()
        .filter(c -> c.workOrderId == workOrderId)
        .max(Comparator.comparingInt(c -> c.attemptNo));
  }

  public List<WorkOrderCloseout> findAll() {
    return store.values().stream()
        .sorted(Comparator.comparing((WorkOrderCloseout c) -> c.workOrderId).thenComparingInt(c -> c.attemptNo))
        .toList();
  }

  public WorkOrderCloseout save(WorkOrderCloseout closeout) {
    if (closeout.id == null) {
      closeout.id = idSeq.incrementAndGet();
      closeout.attemptNo = (int) store.values().stream().filter(c -> c.workOrderId == closeout.workOrderId).count() + 1;
    }
    store.put(closeout.id, closeout);
    return closeout;
  }

  /**
   * 释放该工单被阻塞（BLOCKED）的收尾记录，释放后可以再次提交收尾。
   * APPROVED 的记录不释放，完工结果保持幂等。
   *
   * @return 被释放的记录，没有可释放记录时为空
   */
  public Optional<WorkOrderCloseout> releaseBlockedByWorkOrderId(long workOrderId) {
    Optional<WorkOrderCloseout> target = store.values().stream()
        .filter(c -> c.workOrderId == workOrderId && !c.released)
        .filter(c -> CloseoutOutcome.BLOCKED.name().equals(c.outcome))
        .findFirst();
    target.ifPresent(c -> c.released = true);
    return target;
  }
}
