package com.generated.qualityTrace.repositories;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.InspectionResultStatus;
import com.generated.qualityTrace.constants.InspectionType;
import com.generated.qualityTrace.models.QualityInspection;

@Repository
public class QualityInspectionRepository {

  private final ConcurrentHashMap<Long, QualityInspection> store = new ConcurrentHashMap<>();

  public QualityInspectionRepository() {
    save(new QualityInspection(1L, 1L, "INSP-01", InspectionType.FIRST.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-20T09:30:00+08:00"));
    save(new QualityInspection(2L, 1L, "INSP-01", InspectionType.FINAL.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-20T11:00:00+08:00"));
    save(new QualityInspection(3L, 2L, "INSP-02", InspectionType.PATROL.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-20T14:00:00+08:00"));
    save(new QualityInspection(4L, 2L, "INSP-02", InspectionType.FINAL.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-20T17:00:00+08:00"));
    save(new QualityInspection(5L, 3L, "INSP-01", InspectionType.FINAL.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-21T11:00:00+08:00"));
    save(new QualityInspection(6L, 4L, "INSP-03", InspectionType.FINAL.name(), "SIP-1.2", InspectionResultStatus.PASS.name(), "2026-09-21T17:00:00+08:00"));
    save(new QualityInspection(7L, 5L, "INSP-02", InspectionType.FINAL.name(), "SIP-1.2", InspectionResultStatus.FAIL.name(), "2026-09-22T12:00:00+08:00"));
  }

  public List<QualityInspection> findAll() {
    return store.values().stream().sorted(Comparator.comparing(i -> i.id)).toList();
  }

  public List<QualityInspection> findByBatchId(long batchId) {
    return store.values().stream()
        .filter(i -> i.batchId == batchId)
        .sorted(Comparator.comparing(i -> i.id))
        .toList();
  }

  /** 批次最新一次末检（FINAL），完工收尾只认这条结论。 */
  public Optional<QualityInspection> findLatestFinalByBatchId(long batchId) {
    return store.values().stream()
        .filter(i -> i.batchId == batchId && InspectionType.FINAL.name().equals(i.inspectionType))
        .max(Comparator.comparing((QualityInspection i) -> i.inspectedAt).thenComparing(i -> i.id));
  }

  public QualityInspection save(QualityInspection inspection) {
    store.put(inspection.id, inspection);
    return inspection;
  }
}
