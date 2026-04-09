package com.amsssv.debt_monitor.service;

import com.amsssv.debt_monitor.entity.Contact;
import com.amsssv.debt_monitor.entity.ContactType;
import com.amsssv.debt_monitor.entity.TelegramUser;
import com.amsssv.debt_monitor.repository.ContactRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ContactService {

    private final ContactRepository contactRepository;

    public Contact save(String name, ContactType type, TelegramUser user) {
        Contact contact = new Contact();
        contact.setName(name);
        contact.setType(type);
        contact.setUser(user);
        contact.setCreatedAt(LocalDateTime.now());
        return contactRepository.save(contact);
    }

    public Optional<Contact> findByNameAndTelegramUserId(String name, Long telegramUserId) {
        return contactRepository.findByNameAndUser_TelegramUserId(name, telegramUserId);
    }

    public List<Contact> findByTelegramUserIdAndType(Long telegramUserId, ContactType type) {
        return contactRepository.findByUser_TelegramUserIdAndType(telegramUserId, type);
    }

    public List<Contact> findWithDebtsByTelegramUserId(Long telegramUserId) {
        return contactRepository.findWithDebtsByTelegramUserId(telegramUserId);
    }

    public Optional<Contact> findById(Long id) {
        return contactRepository.findById(id);
    }
}
