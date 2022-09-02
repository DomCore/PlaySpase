package com.example.templates.model;


import java.util.Date;
import java.util.List;

import java.text.SimpleDateFormat;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "lots")
public class Lot implements Comparable<Lot>, Cloneable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;
  @Column(name = "name")
  private String name;
  @Column(name = "seller_id")
  private Integer seller_id;
  @Column(name = "buyer_id")
  private Integer buyer_id;
  @Column(name = "category_id")
  private Integer category_id;
  @Column(name = "cost")
  private String cost;
  @Column(name = "count")
  private double count;
  @Column(name = "status")
  private String status;
  private String date;
  @ElementCollection
  private List<String> templates;
  @ElementCollection
  private List<String> subTemplates;


@Override
  public int compareTo(Lot m){
  try {
    Date date = new SimpleDateFormat("HH:mm dd.MM").parse(m.date);
    Date current = new SimpleDateFormat("HH:mm dd.MM").parse(getDate());
    if (current == null || date == null) {
      return 0;
    }
    return current.compareTo(date);
  } catch (Exception e) {

  }
return 0;
  }

  @Override
  public Lot clone() {
    try {
      // TODO: copy mutable state here, so the clone can't change the internals of the original
      return (Lot) super.clone();
    } catch (CloneNotSupportedException e) {
      throw new AssertionError();
    }
  }

}