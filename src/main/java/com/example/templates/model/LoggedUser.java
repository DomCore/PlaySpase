package com.example.templates.model;

import java.util.List;

import java.io.Serializable;
import javax.servlet.http.HttpSessionBindingEvent;
import javax.servlet.http.HttpSessionBindingListener;

import org.springframework.stereotype.Component;

@Component
public class LoggedUser implements HttpSessionBindingListener, Serializable {

  private static final long serialVersionUID = 1L;
  private String username;
  private ActiveUserStore activeUserStore;

  public LoggedUser(String username, ActiveUserStore activeUserStore) {
    this.username = username;
    this.activeUserStore = activeUserStore;
  }

  public LoggedUser() {}

  @Override
  public void valueBound(HttpSessionBindingEvent event) {
    List<String> users = activeUserStore.getUsers();
    LoggedUser user = (LoggedUser) event.getValue();
    if (!users.contains(user.getUsername())) {
      users.add(user.getUsername());
    }
  }

  @Override
  public void valueUnbound(HttpSessionBindingEvent event) {
    List<String> users = activeUserStore.getUsers();
    LoggedUser user = (LoggedUser) event.getValue();
    if (users.contains(user.getUsername())) {
      users.remove(user.getUsername());
    }
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public ActiveUserStore getActiveUserStore() {
    return activeUserStore;
  }

  public void setActiveUserStore(ActiveUserStore activeUserStore) {
    this.activeUserStore = activeUserStore;
  }
// standard getter and setter
}