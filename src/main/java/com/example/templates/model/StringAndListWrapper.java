package com.example.templates.model;

import java.util.List;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StringAndListWrapper {
  public String string;
  public List<String> arrString;

  public StringAndListWrapper(String string) {
    this.string = string;
  }

  public String getString() {
    return string;
  }

  public void setString(String string) {
    this.string = string;
  }
}
