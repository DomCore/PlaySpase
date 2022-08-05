package com.example.templates.service;

import java.util.List;

import com.example.templates.model.CheckOutHistory;
import com.example.templates.model.RefHistory;
import com.example.templates.repository.CheckRepository;
import com.example.templates.repository.RefRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CheckService {

  private CheckRepository checkRepository;

  @Autowired
  public CheckService(CheckRepository checkRepository) {
    this.checkRepository = checkRepository;
  }

  public List<CheckOutHistory> findByTargetId(Integer id) {
    return checkRepository.findByTargetId(id);
  }
  public void saveCheckHistory(CheckOutHistory checkOutHistory) {
    checkRepository.save(checkOutHistory);
  }
}
