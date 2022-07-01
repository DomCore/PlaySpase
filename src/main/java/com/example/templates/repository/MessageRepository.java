package com.example.templates.repository;

import java.util.List;
import java.util.Optional;

import com.example.templates.model.ChatMessage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MessageRepository extends JpaRepository<ChatMessage, Integer> {

  @Query("SELECT c FROM ChatMessage c WHERE c.sender_id = ?1 or c.receiver_id =?1")
  List<ChatMessage> findByUser(Integer id);
  @Query("SELECT c FROM ChatMessage c WHERE c.sender_id = ?1 and c.receiver_id = ?2 or c.receiver_id =?1 and c.sender_id = ?2")
  List<ChatMessage> findByUsers(Integer ids1, Integer ids2);
}