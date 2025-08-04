package com.amsssv.debt_monitor.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Debt {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Integer amount;

  @ManyToOne
  @JoinColumn(name = "lender_id")
  @JsonIgnore
  private Lender lender;

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
