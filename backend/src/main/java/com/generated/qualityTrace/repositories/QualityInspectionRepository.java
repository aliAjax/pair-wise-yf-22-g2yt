package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.models.QualityInspection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import org.springframework.stereotype.Repository;

@Repository
public class QualityInspectionRepository {
  private final List<QualityInspection> store = new CopyOnWriteArrayList<>();

  public QualityInspectionRepository() {
    store.add(new QualityInspection(1001L, 101L, "inspector1", InspectionType.FIRST.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-18T10:30:00Z"));
    store.add(new QualityInspection(1002L, 101L, "inspector1", InspectionType.FINAL.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-18T18:00:00Z"));
    store.add(new QualityInspection(1003L, 102L, "inspector1", InspectionType.FINAL.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-18T19:00:00Z"));
    store.add(new QualityInspection(1004L, 201L, "inspector1", InspectionType.PATROL.name(), "SIP-2.0", InspectionResultStatus.PASS.name(), "2026-09-19T11:00:00Z"));
    store.add(new QualityInspection(1005L, 201L, "inspector1", InspectionType.FINAL.name(), "SIP-2.0", InspectionResultStatus.PASS.name(), "2026-09-19T17:00:00Z"));
    store.add(new QualityInspection(1006L, 202L, "inspector1", InspectionType.FINAL.name(), "SIP-2.0", InspectionResultStatus.PASS.name(), "2026-09-19T18:00:00Z"));
    store.add(new QualityInspection(1007L, 301L, "inspector1", InspectionType.FINAL.name(), "SIP-3.1", InspectionResultStatus.PASS.name(), "2026-09-20T10:00:00Z"));
    store.add(new QualityInspection(1008L, 301L, "inspector1", InspectionType.FINAL.name(), "SIP-3.1", InspectionResultStatus.FAIL.name(), "2026-09-20T12:00:00Z"));
  }

  public List<QualityInspection> findAll() {
    return List.copyOf(store);
  }

  /** 批次最近一次末检（FINAL）记录 */
  public Optional<QualityInspection> findLatestFinalByBatchId(long batchId) {
    return store.stream()
        .filter(i -> i.batchId == batchId && InspectionType.FINAL.name().equals(i.inspectionType))
        .max(Comparator.comparing((QualityInspection i) -> i.inspectedAt).thenComparing(i -> i.id));
  }
}
