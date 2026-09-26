package com.generated.qualityTrace.repositories;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.springframework.stereotype.Repository;
import com.generated.qualityTrace.constants.DefectDispositionStatus;
import com.generated.qualityTrace.constants.DefectSeverity;
import com.generated.qualityTrace.models.DefectRecord;

@Repository
public class DefectRecordRepository {

  private final ConcurrentHashMap<Long, DefectRecord> store = new ConcurrentHashMap<>();

  public DefectRecordRepository() {
    save(new DefectRecord(1L, 2L, "裂纹", 2, DefectSeverity.CRITICAL.name(), "模具老化", DefectDispositionStatus.OPEN.name()));
    save(new DefectRecord(2L, 2L, "划痕", 5, DefectSeverity.MINOR.name(), "转运磕碰", DefectDispositionStatus.OPEN.name()));
    save(new DefectRecord(3L, 2L, "尺寸超差", 1, DefectSeverity.MAJOR.name(), "刀具磨损", DefectDispositionStatus.CLOSED.name()));
    save(new DefectRecord(4L, 3L, "毛刺", 3, DefectSeverity.MAJOR.name(), "去毛刺工序遗漏", DefectDispositionStatus.OPEN.name()));
    save(new DefectRecord(5L, 5L, "气孔", 4, DefectSeverity.CRITICAL.name(), "浇注温度偏低", DefectDispositionStatus.CLOSED.name()));
  }

  public List<DefectRecord> findAll() {
    return store.values().stream().sorted(Comparator.comparing(d -> d.id)).toList();
  }

  public Optional<DefectRecord> findById(long id) {
    return Optional.ofNullable(store.get(id));
  }

  public List<DefectRecord> findByBatchId(long batchId) {
    return store.values().stream()
        .filter(d -> d.batchId == batchId)
        .sorted(Comparator.comparing(d -> d.id))
        .toList();
  }

  /** 指定批次集合内仍未关闭（非 CLOSED）的严重不良。 */
  public List<DefectRecord> findOpenCriticalByBatchIds(Collection<Long> batchIds) {
    return store.values().stream()
        .filter(d -> batchIds.contains(d.batchId))
        .filter(d -> DefectSeverity.CRITICAL.name().equals(d.severity))
        .filter(d -> !DefectDispositionStatus.CLOSED.name().equals(d.dispositionStatus))
        .sorted(Comparator.comparing(d -> d.id))
        .toList();
  }

  public DefectRecord save(DefectRecord defect) {
    store.put(defect.id, defect);
    return defect;
  }
}
