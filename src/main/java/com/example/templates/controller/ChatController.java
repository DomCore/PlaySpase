package com.example.templates.controller;

import java.text.SimpleDateFormat;

import com.example.templates.model.ChatMessage;
import com.example.templates.service.ChatMessageService;
import com.example.templates.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.stereotype.Controller;
import java.sql.Timestamp;

import java.util.Date;
import java.util.List;

@Controller
public class ChatController {
  private static final SimpleDateFormat sdf = new SimpleDateFormat("HH:mm dd.MM");
  @Autowired
  ChatMessageService chatMessageService;
  @Autowired
  UserService userService;

  @MessageMapping("/chat.register")
  @SendTo("/topic/public")
  public ChatMessage register(@Payload ChatMessage chatMessage, SimpMessageHeaderAccessor headerAccessor) {
    headerAccessor.getSessionAttributes().put("username", chatMessage.getSender());
    return chatMessage;
  }

  @MessageMapping("/chat.send")
  @SendTo("/topic/public")
  public ChatMessage sendMessage(@Payload ChatMessage chatMessage) {
    Date date = new Date();
    chatMessage.setTime(sdf.format(new Timestamp(date.getTime())));
    chatMessage.setSender_id(userService.findUserByUserName(chatMessage.getSender()).getId());
    chatMessage.setReceiver_id(userService.findUserByUserName(chatMessage.getReceiver()).getId());
    chatMessage.setChecked(false);
    chatMessage.setChat_id("{"+Math.min(chatMessage.getSender_id(),chatMessage.getReceiver_id())+"} {"+Math.max(chatMessage.getSender_id(),chatMessage.getReceiver_id())+"}");
    chatMessageService.save(chatMessage);
    return chatMessage;
  }

  @MessageMapping("/chat.check")
  @SendTo("/topic/public")
  public void checkMessage(@Payload ChatMessage chatMessage) {
    List<ChatMessage> msgs = chatMessageService.findByUsers(userService.findUserByUserName(chatMessage.getSender()).getId(), userService.findUserByUserName(chatMessage.getReceiver()).getId());
    msgs.forEach(msg -> {
      msg.setChecked(true);
      chatMessageService.save(msg);
    });
  }

}
