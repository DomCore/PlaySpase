package com.example.templates.repository;

import java.util.List;
import java.util.Optional;

import com.example.templates.model.Category;
import com.example.templates.model.RefHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface RefRepository extends JpaRepository<RefHistory, Integer> {

  @Query("SELECT c FROM RefHistory c WHERE c.target_id = ?1")
  List<RefHistory> findByTargetId(Integer id);

}