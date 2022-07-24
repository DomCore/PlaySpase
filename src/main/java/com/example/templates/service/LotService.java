package com.example.templates.service;

import java.util.List;
import java.util.stream.Collectors;

import com.example.templates.model.Category;
import com.example.templates.model.Lot;
import com.example.templates.model.User;
import com.example.templates.repository.CategoryRepository;
import com.example.templates.repository.LotRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LotService {

  private LotRepository lotRepository;

  @Autowired
  UserService userService;

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
  public Lot getById(Integer id)  {
    return lotRepository.getById(id);
  }
  public int getMaxId()  {
    return lotRepository.getMaxId();
  }
  public List<Lot> getBySeller_id(Integer id)  {
    return lotRepository.getBySeller_id(id);
  }
  public void deleteByCategoryId(Integer id)  {
    lotRepository.deleteByCategoryId(id);
  }
  public void deleteById(Integer id)  {
    lotRepository.deleteById(id);
  }

  public List<Lot> getByBuyer_id(Integer id) {
    return lotRepository.getByBuyer_id(id);
  }


  public Integer getSells(Integer id) {
    List<Lot> lots = getBySeller_id(id).stream().filter(l -> l.getStatus().equals("Подтверждение")).collect(Collectors.toList());
    return lots.size();
  }
  public Integer getBuys(Integer id) {
    List<Lot> lots = getByBuyer_id(id).stream().filter(l -> l.getStatus().equals("Подтверждение")).collect(Collectors.toList());
    return lots.size();
  }
}
