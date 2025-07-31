package com.amsssv.debt_monitor.entity;


import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "telegram_users")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class TelegramUser {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  private Long telegramUserId;
  private String userName;
  private String firstName;
  private String lastName;
  private Long chatId;

//  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
//  private List<Debt> debts =  new ArrayList<>();

  @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  private List<Lender> lenders =  new ArrayList<>();

  private LocalDateTime createdAt;
  private LocalDateTime updatedAt;
}
