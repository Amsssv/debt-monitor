package com.amsssv.debt_monitor.service;

import com.amsssv.debt_monitor.entity.Contact;
import com.amsssv.debt_monitor.entity.ContactType;
import com.amsssv.debt_monitor.entity.Debt;
import com.amsssv.debt_monitor.repository.DebtRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class DebtService {

    private final DebtRepository debtRepository;

    public void save(Integer amount, ContactType type, Contact contact) {
        Debt debt = new Debt();
        debt.setAmount(amount);
        debt.setType(type);
        debt.setContact(contact);
        debt.setCreatedAt(LocalDateTime.now());
        debtRepository.save(debt);
    }
}
