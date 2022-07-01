package com.example.templates.model;

import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ChatWrapper implements Comparable<ChatWrapper>{

  public List<ChatMessage> messages;
  public String name;
  public String logo;
  public String message;
  public String user;
  public String time;
  public Integer id;

  public ChatWrapper(List<ChatMessage> messages) {
    this.messages = messages;
  }

  public ChatWrapper(List<ChatMessage> messages, String name) {
    this.messages = messages;
    this.name = name;
  }

  public ChatWrapper(List<ChatMessage> messages, String name, String logo) {
    this.messages = messages;
    this.name = name;
    this.logo = logo;
  }

  public ChatWrapper(List<ChatMessage> messages, String name, String logo, String message, String user) {
    this.messages = messages;
    this.name = name;
    this.logo = logo;
    this.message = message;
    this.user = user;
  }

  public ChatWrapper(List<ChatMessage> messages, String name, String logo, String message, String user, String time) {
    this.messages = messages;
    this.name = name;
    this.logo = logo;
    this.message = message;
    this.user = user;
    this.time = time;
  }

  public ChatWrapper(List<ChatMessage> messages, String name, String logo, String message, String user, String time, Integer id) {
    this.messages = messages;
    this.name = name;
    this.logo = logo;
    this.message = message;
    this.user = user;
    this.time = time;
    this.id = id;
  }

  @Override
  public int compareTo(ChatWrapper c) {
    return getId().compareTo(c.getId());
  }
}
