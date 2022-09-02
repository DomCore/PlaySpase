package com.example.templates.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "chatmessage")
public class ChatMessage implements Comparable<ChatMessage>{
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
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
  @Column(name = "systemMessage")
  private boolean system;
  @Column(name = "checked")
  private boolean checked;
  private MessageType type;
  private String file;

  public String getFile() {
    return file;
  }

  public void setFile(String file) {
    this.file = file;
  }

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

  public boolean isChecked() {
    return checked;
  }

  public void setChecked(boolean read) {
    this.checked = read;
  }

  public boolean isSystem() {
    return system;
  }

  public void setSystem(boolean system) {
    this.system = system;
  }

  @Override
  public int compareTo(ChatMessage m) {
    if (getId() == null || m.getId() == null) {
      return 0;
    }
    return getId().compareTo(m.getId());
  }
}