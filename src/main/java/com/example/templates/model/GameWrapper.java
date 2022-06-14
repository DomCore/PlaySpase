package com.example.templates.model;

import java.util.List;

public class GameWrapper {
  private Game game;
  public List<Category> categories;

  public GameWrapper(Game game, List<Category> categories) {
    this.game = game;
    this.categories = categories;
  }

  public Game getGame() {
    return game;
  }

  public void setGame(Game game) {
    this.game = game;
  }

  public List<Category> getCategories() {
    return categories;
  }

  public void setCategories(List<Category> categories) {
    this.categories = categories;
  }
}
