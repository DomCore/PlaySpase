package com.example.templates.repository;

import java.util.List;
import java.util.Optional;

import com.example.templates.model.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
@Repository
public interface GameRepository extends JpaRepository<Game, Integer> {
  Game findByName(String name);
  @Query("SELECT g FROM Game g WHERE g.id = ?1")
  Optional<Game> findById(Integer id);

  @Query("SELECT g FROM Game g WHERE g.id > 0")
  List<Game> getAll();

  @Modifying
  @Transactional
  @Query("Update Game g SET g.name = ?1, g.tags=?2 where g.id = ?3")
  void editGame(String name, String tags, Integer id);

  @Modifying
  @Transactional
  @Query("delete from Game g where g.id = ?1")
  void deleteGameById(Integer id);
}