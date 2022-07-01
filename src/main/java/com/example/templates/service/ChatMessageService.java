package com.example.templates.service;

import java.util.List;

import com.example.templates.model.ChatMessage;
import com.example.templates.model.Game;
import com.example.templates.repository.GameRepository;
import com.example.templates.repository.MessageRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ChatMessageService {

  private MessageRepository messageRepository;

  @Autowired
  public ChatMessageService(MessageRepository messageRepository) {
    this.messageRepository = messageRepository;
  }
  public List<ChatMessage> findByUser(Integer  id) {
    return messageRepository.findByUser(id);
  }
  public List<ChatMessage> findByUsers(Integer  id1, Integer id2) {
    return messageRepository.findByUsers(id1, id2);
  }

  public void save(ChatMessage chatMessage) {
    this.messageRepository.save(chatMessage);
  }
}
