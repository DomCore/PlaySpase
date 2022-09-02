package com.example.templates.repository;

import java.util.List;

import com.example.templates.model.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Integer> {

  @Query("SELECT c FROM Feedback c WHERE c.target_id = ?1")
  List<Feedback> findByTargetId(Integer id);

  @Query("SELECT c FROM Feedback c WHERE c.lot_id = ?1")
  Feedback findByLotId(Integer id);
}