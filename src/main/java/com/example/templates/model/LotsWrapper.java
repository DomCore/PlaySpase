package com.example.templates.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class LotsWrapper {
  public Integer id;
  public String cost;
  public String seller;
  public String game;
  public String category;
  public List<String> keys;
  public List<String> values;

  public LotsWrapper(Integer id, String cost, String seller, String game, String category, List<String> keys, List<String> values) {
    this.id = id;
    this.cost = cost;
    this.seller = seller;
    this.game = game;
    this.category = category;
    this.keys = keys;
    this.values = values;
  }

  public String getGame() {
    return game;
  }

  public LotsWrapper(Integer id, String cost, String game, String category, List<String> keys, List<String> values) {
    this.id = id;
    this.cost = cost;
    this.game = game;
    this.category = category;
    this.keys = keys;
    this.values = values;
  }

  public void setGame(String game) {
    this.game = game;
  }

  public String getCategory() {
    return category;
  }

  public void setCategory(String category) {
    this.category = category;
  }

  public LotsWrapper(Integer id, String cost, String seller, List<String> keys, List<String> values) {
    this.id = id;
    this.cost = cost;
    this.seller = seller;
    this.keys = keys;
    this.values = values;
  }

  public String getCost() {
    return cost;
  }

  public void setCost(String cost) {
    this.cost = cost;
  }

  public String getSeller() {
    return seller;
  }

  public void setSeller(String seller) {
    this.seller = seller;
  }

  public LotsWrapper(Integer id, List<String> keys, List<String> values) {
    this.id = id;
    this.keys = keys;
    this.values = values;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public List<String> getKeys() {
    return keys;
  }

  public void setKeys(List<String> keys) {
    this.keys = keys;
  }

  public List<String> getValues() {
    return values;
  }

  public void setValues(List<String> values) {
    this.values = values;
  }
}
