package com.example.templates.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chatmessage")
public class ChatMessage implements Comparable<ChatMessage>{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;
  @Column(name = "content")
  private String content;
  @Column(name = "sender_id")
  private Integer sender_id;
  @Column(name = "receiver_id")
  private Integer receiver_id;
  private String chat_id;
  private String time;
  private String sender;
  private String receiver;
  private String logo;
  private MessageType type;

  public enum MessageType {
    CHAT, LEAVE, JOIN
  }

  public String getLogo() {
    return logo;
  }

  public void setLogo(String logo) {
    this.logo = logo;
  }

  public String getReceiver() {
    return receiver;
  }

  public void setReceiver(String receiver) {
    this.receiver = receiver;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getSender() {
    return sender;
  }

  public void setSender(String sender) {
    this.sender = sender;
  }

  public MessageType getType() {
    return type;
  }

  public void setType(MessageType type) {
    this.type = type;
  }

  @Override
  public int compareTo(ChatMessage m) {
    if (getChat_id() == null || m.getChat_id() == null) {
      return 0;
    }
    return getChat_id().compareTo(m.getChat_id());
  }
}