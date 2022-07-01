package com.example.templates.model;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Transient;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "games")
public class Game implements Comparable<Game>{

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;
  @Column(name = "name")
  @NotEmpty(message = "*Введите название")
  private String name;
  @Column(name = "tags")
  @NotEmpty(message = "*Введите теги для поиска")
  private String tags;
  @Column(name = "logo",nullable = true, length = 64)
  private String logo;
  @Column(name = "top",nullable = true)
  private boolean top;
  @ElementCollection
  private List<String> tagsArray;

  @Transient
  public String getPhotosImagePath() {
    if (logo == null || id == null) return null;

    return "/game_logos/" + id + "/" + logo;
  }

  @Override
  public int compareTo(Game g) {
    if (getName() == null || g.getName() == null) {
      return 0;
    }
    return getName().compareTo(g.getName());
  }
}