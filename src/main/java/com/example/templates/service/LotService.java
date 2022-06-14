package com.example.templates.service;

import java.util.List;

import com.example.templates.model.Category;
import com.example.templates.model.Lot;
import com.example.templates.repository.CategoryRepository;
import com.example.templates.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LotService {

  private LotRepository lotRepository;

  @Autowired
  public LotService(LotRepository lotRepository) {
    this.lotRepository = lotRepository;
  }

  public void saveLot(Lot lot) {
    lotRepository.save(lot);
  }

  public List<Lot> getByCategoryId(Integer id)  {
    return lotRepository.getByCategoryId(id);
  }
}
