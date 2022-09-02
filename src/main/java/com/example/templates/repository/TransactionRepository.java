package com.example.templates.repository;

import java.util.List;

import com.example.templates.model.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Integer> {

  @Query("SELECT c FROM Transaction c WHERE c.user_id = ?1")
  List<Transaction> findByTargetId(Integer id);

}