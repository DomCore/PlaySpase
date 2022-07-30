package com.example.templates.service;

import java.util.List;
import com.example.templates.model.RefHistory;
import com.example.templates.model.User;
import com.example.templates.repository.CategoryRepository;
import com.example.templates.repository.RefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RefService {

  private RefRepository refRepository;

  @Autowired
  public RefService(RefRepository refRepository) {
    this.refRepository = refRepository;
  }

  public List<RefHistory> findByTargetId(Integer id) {
    return refRepository.findByTargetId(id);
  }
  public void saveRefHistory(RefHistory refHistory) {
    refRepository.save(refHistory);
  }
}
