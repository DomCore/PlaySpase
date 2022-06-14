package com.example.templates.repository;

import java.util.List;

import com.example.templates.model.Category;
import com.example.templates.model.Lot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface LotRepository extends JpaRepository<Lot, Integer> {

  @Query("SELECT l FROM Lot l WHERE l.category_id = ?1")
  List<Lot> getByCategoryId(Integer id);
  @Query("SELECT l FROM Lot l WHERE l.seller_id = ?1")
  List<Lot> getBySeller_id(Integer id);
  @Query("SELECT l FROM Lot l WHERE l.id = ?1")
  Lot getById(Integer id);
  @Modifying
  @Transactional
  @Query("DELETE FROM Lot l WHERE l.category_id = ?1")
  void deleteByCategoryId(Integer id);

}