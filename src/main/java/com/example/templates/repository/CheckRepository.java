package com.example.templates.repository;

import java.util.List;

import com.example.templates.model.CheckOutHistory;
import com.example.templates.model.RefHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckRepository extends JpaRepository<CheckOutHistory, Integer> {

  @Query("SELECT c FROM CheckOutHistory c WHERE c.target_id = ?1")
  List<CheckOutHistory> findByTargetId(Integer id);

}