package com.generated.qualityTrace.repositories;

import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.models.DefectRecord;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;
import org.springframework.stereotype.Repository;

@Repository
public class DefectRecordRepository {
  private final List<DefectRecord> store = new CopyOnWriteArrayList<>();

  public DefectRecordRepository() {
    store.add(new DefectRecord(301L, 201L, "CRACK", 5, DefectSeverity.CRITICAL.name(), "热处理温度偏高", DefectDispositionStatus.OPEN.name()));
    store.add(new DefectRecord(302L, 201L, "SCRATCH", 12, DefectSeverity.MINOR.name(), "搬运磕碰", DefectDispositionStatus.OPEN.name()));
    store.add(new DefectRecord(303L, 102L, "DIMENSION", 2, DefectSeverity.MINOR.name(), "刀具磨损", DefectDispositionStatus.CLOSED.name()));
    store.add(new DefectRecord(304L, 202L, "BURR", 8, DefectSeverity.MAJOR.name(), "模具老化", DefectDispositionStatus.OPEN.name()));
  }

  public List<DefectRecord> findAll() {
    return List.copyOf(store);
  }

  public Optional<DefectRecord> findById(long id) {
    return store.stream().filter(d -> d.id == id).findFirst();
  }

  /** 批次未关闭（dispositionStatus != CLOSED）的不良记录 */
  public List<DefectRecord> findOpenByBatchId(long batchId) {
    return store.stream()
        .filter(d -> d.batchId == batchId)
        .filter(d -> !DefectDispositionStatus.CLOSED.name().equals(d.dispositionStatus))
        .collect(Collectors.toList());
  }

  public void save(DefectRecord defect) {
    for (int i = 0; i < store.size(); i++) {
      if (store.get(i).id.equals(defect.id)) {
        store.set(i, defect);
        return;
      }
    }
    store.add(defect);
  }
}
