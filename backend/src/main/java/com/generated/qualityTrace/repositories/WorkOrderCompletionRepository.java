package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.models.WorkOrderCompletion;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Repository;

/** 完工收尾记录：每个工单只保留第一次成功提交的结果，用于幂等重放 */
@Repository
public class WorkOrderCompletionRepository {
  private final ConcurrentHashMap<Long, WorkOrderCompletion> store = new ConcurrentHashMap<>();
  private final AtomicLong idSeq = new AtomicLong(1);

  public Optional<WorkOrderCompletion> findByWorkOrderId(long workOrderId) {
    return Optional.ofNullable(store.get(workOrderId));
  }

  public WorkOrderCompletion save(WorkOrderCompletion completion) {
    if (completion.id == null) {
      completion.id = idSeq.getAndIncrement();
    }
    store.put(completion.workOrderId, completion);
    return completion;
  }
}
