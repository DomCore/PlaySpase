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
  public List<String> keys;
  public List<String> values;

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
