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

import groovy.transform.Sortable;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class Transaction implements Comparable<Transaction> {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @Column(name = "id")
  private Integer id;
  private Integer lot_id;
  private Integer user_id;
  private String value;
  private String date;
  private String type;
  private String status;
  private String card;
  @Transient
  private User user;

  @Override
  public int compareTo(Transaction g) {
    if (getId() == null || g.getId() == null) {
      return 0;
    }
    return getId().compareTo(g.getId());
  }
}