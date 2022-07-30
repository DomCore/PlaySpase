package com.example.templates.model;


import java.util.List;

import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.validation.constraints.NotEmpty;

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
  public int compareTo(Lot m) {
    if (getId() == null || m.getId() == null) {
      return 0;
    }
    return getId().compareTo(m.getId());
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