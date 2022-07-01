package com.example.templates.model;

import java.util.List;

public class GameWrapper {
  private Game game;
  public String gameId;
  public Category category;
  public List<Category> categories;

  public GameWrapper(String gameId, Category category) {
    this.gameId = gameId;
    this.category = category;
  }

  public Category getCategory() {
    return category;
  }

  public void setCategory(Category category) {
    this.category = category;
  }

  public GameWrapper(Game game, List<Category> categories) {
    this.game = game;
    this.categories = categories;
  }
  public GameWrapper(String gameId, List<Category> categories) {
    this.gameId = gameId;
    this.categories = categories;
  }

  public String getGameId() {
    return gameId;
  }

  public void setGameId(String gameId) {
    this.gameId = gameId;
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
