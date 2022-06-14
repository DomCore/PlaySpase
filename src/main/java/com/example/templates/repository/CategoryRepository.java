package com.example.templates.repository;

import java.util.List;
import java.util.Optional;

import com.example.templates.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Integer> {
  Category findByName(String name);

  @Query("SELECT c FROM Category c WHERE c.id > 0")
  List<Category> getAll();

  @Query("SELECT c FROM Category c WHERE c.game_id = ?1")
  List<Category> findByGameId(Integer id);
  @Query("SELECT c FROM Category c WHERE c.id = ?1")
  Optional<Category> findById(Integer id);

  @Modifying
  @Transactional
  @Query("delete from Category c where c.id = ?1")
  void deleteCategoryById(Integer id);
}