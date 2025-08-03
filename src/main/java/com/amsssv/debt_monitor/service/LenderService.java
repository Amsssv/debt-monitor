package com.amsssv.debt_monitor.service;

import com.amsssv.debt_monitor.entity.Lender;
import com.amsssv.debt_monitor.entity.TelegramUser;
import com.amsssv.debt_monitor.repository.LenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class LenderService {

  private final LenderRepository lenderRepository;

  public Lender save(String name, TelegramUser user) {
    Lender lender = new Lender();
    lender.setName(name);
    lender.setUser(user);
    lender.setCreatedAt(LocalDateTime.now());
    return lenderRepository.save(lender);
  }
}
