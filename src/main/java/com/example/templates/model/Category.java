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

import org.hibernate.validator.constraints.Length;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "categories")
public class Category {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;
  @Column(name = "name")
  @NotEmpty(message = "*Введите название")
  private String name;
  @Column(name = "template")
  @NotEmpty(message = "*Введите название")
  private String templates;
  @Column(name = "game_id")
  private Integer game_id;
  private Integer tax;
  @ElementCollection
  private List<String> subTemplates;
}