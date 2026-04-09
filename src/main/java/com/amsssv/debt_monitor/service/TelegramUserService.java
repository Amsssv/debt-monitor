package com.amsssv.debt_monitor.service;

import com.amsssv.debt_monitor.entity.Contact;
import com.amsssv.debt_monitor.entity.TelegramUser;
import com.amsssv.debt_monitor.repository.TelegramUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TelegramUserService {

    private final TelegramUserRepository telegramUserRepository;
    private final ContactService contactService;

    public void save(Long telegramUserId, String userName, String firstName, String lastName, Long chatId) {
        TelegramUser telegramUser = new TelegramUser();
        telegramUser.setTelegramUserId(telegramUserId);
        telegramUser.setUserName(userName);
        telegramUser.setFirstName(firstName);
        telegramUser.setLastName(lastName);
        telegramUser.setChatId(chatId);
        telegramUser.setCreatedAt(LocalDateTime.now());
        telegramUserRepository.save(telegramUser);
    }

    public Optional<TelegramUser> findByTelegramUserId(Long telegramUserId) {
        return telegramUserRepository.findByTelegramUserId(telegramUserId);
    }

    public Contact addContact(String name, Long telegramUserId) {
        TelegramUser user = telegramUserRepository.findByTelegramUserId(telegramUserId)
                .orElseThrow(() -> new IllegalStateException("User not found: " + telegramUserId));
        return contactService.save(name, user);
    }
}
