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
public class LotWrapper {
  public Lot lot;
  public List<StringAndListWrapper> list;
}
