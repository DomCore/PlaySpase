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
  @Query("SELECT g FROM Game g WHERE g.top = true")
  List<Game> getAllTop();
  @Query("SELECT g FROM Game g WHERE g.top = false")
  List<Game> getAllDownTop();
  @Modifying
  @Transactional
  @Query("Update Game g SET g.name = ?1, g.tags=?2, g.logo=?4, g.top =?5 where g.id = ?3")
  void editGame(String name, String tags, Integer id, String logo, boolean top);

  @Modifying
  @Transactional
  @Query("delete from Game g where g.id = ?1")
  void deleteGameById(Integer id);
}