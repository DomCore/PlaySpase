package com.example.templates.service;

import java.util.List;

import com.example.templates.model.ChatMessage;
import com.example.templates.model.Transaction;
import com.example.templates.repository.TransactionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TransactionService {

  private TransactionRepository transactionRepository;

  @Autowired
  public TransactionService(TransactionRepository transactionRepository) {
    this.transactionRepository = transactionRepository;
  }

  public List<Transaction> findByTargetId(Integer id) {
    return transactionRepository.findByTargetId(id);
  }
  public void save(Transaction transaction) {
    this.transactionRepository.save(transaction);
  }
  public List<Transaction> findAll() {
    return this.transactionRepository.findAll();
  }
  public Transaction findById(Integer id) {
    return this.transactionRepository.findById(id).orElse(null);
  }
}
