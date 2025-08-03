package com.amsssv.debt_monitor.service;

import com.amsssv.debt_monitor.entity.Lender;
import com.amsssv.debt_monitor.entity.TelegramUser;
import com.amsssv.debt_monitor.repository.LenderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

  public Optional<Lender> findByLenderName(String lenderName) {
    return lenderRepository.findByName(lenderName);
  }

  public List<Lender> findAll() {
    return lenderRepository.findAll();
  }
}
