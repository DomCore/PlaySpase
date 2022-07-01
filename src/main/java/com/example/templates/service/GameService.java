package com.example.templates.service;

import java.util.List;

import com.example.templates.model.Game;
import com.example.templates.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class GameService {

  private GameRepository gameRepository;

  @Autowired
  public GameService(GameRepository gameRepository) {
    this.gameRepository = gameRepository;
  }

  public Game findByName(String name) {
    return gameRepository.findByName(name);
  }
  public Game findById(Integer id) {
    return gameRepository.findById(id).get();
  }

  public void editGame(Game g) {
    gameRepository.editGame(g.getName(), g.getTags(), g.getId(), g.getLogo(), g.isTop());
  }
  public void deleteGameById(Integer g) {
    gameRepository.deleteGameById(g);
  }
  public void saveGame(Game game) {
    gameRepository.save(game);
  }

  public List<Game> getAllGames() {
    return gameRepository.getAll();
  }
  public List<Game> getAllTop() {
    return gameRepository.getAllTop();
  }
  public List<Game> getAllDownTop() {
    return gameRepository.getAllDownTop();
  }
}
