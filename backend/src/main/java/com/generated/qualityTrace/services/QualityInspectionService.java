package com.generated.qualityTrace.services;

import com.generated.qualityTrace.constructors.QualityInspectionDtoFactory;
import com.generated.qualityTrace.repositories.QualityInspectionRepository;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class QualityInspectionService {
  private final QualityInspectionRepository repo;

  public QualityInspectionService(QualityInspectionRepository repo) {
    this.repo = repo;
  }

  public List<Map<String, Object>> list() {
    return repo.findAll().stream().map(QualityInspectionDtoFactory::toDto).toList();
  }
}
