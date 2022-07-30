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
  public double count;
  public String seller;
  public String game;
  public String date;
  public String category;
  public String header;
  public String subHeader;
  public String subCost;
  public boolean allowDecimal;
  public boolean showCount;
  public List<String> keys;
  public List<String> values;

  public LotsWrapper(Integer id, String status, double count, String seller, String game, String date, String category, String header, String subHeader, String subCost, boolean allowDecimal, boolean showCount, List<String> keys, List<String> values) {
    this.id = id;
    this.status = status;
    this.count = count;
    this.seller = seller;
    this.game = game;
    this.date = date;
    this.category = category;
    this.header = header;
    this.subHeader = subHeader;
    this.subCost = subCost;
    this.allowDecimal = allowDecimal;
    this.showCount = showCount;
    this.keys = keys;
    this.values = values;
  }

  public LotsWrapper(Integer id, String status, double count, String seller, String game, String date, String category, List<String> keys, List<String> values) {
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
  public LotsWrapper(Integer id, String game, String category, double count, List<String> keys, List<String> values) {
    this.id = id;
    this.game = game;
    this.category = category;
    this.count = count;
    this.keys = keys;
    this.values = values;
  }

  public LotsWrapper(Integer id, String status, double count, String seller, String game, String category, List<String> keys, List<String> values) {
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

  public LotsWrapper(Integer id, double count, String seller, String header,
                     String subHeader, String subCost, boolean allowDecimal, boolean showCount, List<String> keys, List<String> values) {
    this.id = id;
    this.count = count;
    this.seller = seller;
    this.header = header;
    this.subHeader = subHeader;
    this.subCost = subCost;
    this.allowDecimal = allowDecimal;
    this.showCount = showCount;
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

  public Double getCount() {
    return count;
  }

  public void setCount(double count) {
    this.count = count;
  }

  public String getDate() {
    return date;
  }

  public void setDate(String date) {
    this.date = date;
  }

  public String getHeader() {
    return header;
  }

  public void setHeader(String header) {
    this.header = header;
  }

  public String getSubHeader() {
    return subHeader;
  }

  public void setSubHeader(String subHeader) {
    this.subHeader = subHeader;
  }

  public String getSubCost() {
    return subCost;
  }

  public void setSubCost(String subCost) {
    this.subCost = subCost;
  }

  public boolean isAllowDecimal() {
    return allowDecimal;
  }

  public void setAllowDecimal(boolean allowDecimal) {
    this.allowDecimal = allowDecimal;
  }

  public boolean isShowCount() {
    return showCount;
  }

  public void setShowCount(boolean showCount) {
    this.showCount = showCount;
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
