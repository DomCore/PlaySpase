package com.example.templates.model;

import java.util.List;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
public class LotsWrapper {
  public Integer id;
  public String status;
  public int count;
  public String seller;
  public String game;
  public String date;
  public String category;
  public List<String> keys;
  public List<String> values;

  public LotsWrapper(Integer id, String status, int count, String seller, String game, String date, String category, List<String> keys, List<String> values) {
    this.id = id;
    this.status = status;
    this.count = count;
    this.seller = seller;
    this.game = game;
    this.date = date;
    this.category = category;
    this.keys = keys;
    this.values = values;
  }

  public LotsWrapper(Integer id, String status, String seller, String game, String category, List<String> keys, List<String> values) {
    this.id = id;
    this.status = status;
    this.seller = seller;
    this.game = game;
    this.category = category;
    this.keys = keys;
    this.values = values;
  }

  public String getGame() {
    return game;
  }

  public LotsWrapper(Integer id, String seller, String game, String category, List<String> keys, List<String> values) {
    this.id = id;
    this.seller = seller;
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

  public LotsWrapper(Integer id, String game, String category, List<String> keys, List<String> values) {
    this.id = id;
    this.game = game;
    this.category = category;
    this.keys = keys;
    this.values = values;
  }
  public LotsWrapper(Integer id, String game, String category, int count, List<String> keys, List<String> values) {
    this.id = id;
    this.game = game;
    this.category = category;
    this.count = count;
    this.keys = keys;
    this.values = values;
  }

  public LotsWrapper(Integer id, String status, int count, String seller, String game, String category, List<String> keys, List<String> values) {
    this.id = id;
    this.status = status;
    this.count = count;
    this.seller = seller;
    this.game = game;
    this.category = category;
    this.keys = keys;
    this.values = values;
  }
  public LotsWrapper(Integer id, String status, String date, String seller, String game, String category, List<String> keys, List<String> values) {
    this.id = id;
    this.status = status;
    this.seller = seller;
    this.game = game;
    this.category = category;
    this.date = date;
    this.keys = keys;
    this.values = values;
  }
  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
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
