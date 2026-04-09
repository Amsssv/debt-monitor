package com.amsssv.debt_monitor.bot.handler;

import com.amsssv.debt_monitor.bot.Bot;
import com.amsssv.debt_monitor.bot.BotState;
import com.amsssv.debt_monitor.bot.UserSession;
import com.amsssv.debt_monitor.entity.Contact;
import com.amsssv.debt_monitor.entity.ContactType;
import com.amsssv.debt_monitor.service.ContactService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CallbackHandler {

    private final ContactService contactService;

    public void handleCommand(CallbackQuery callbackQuery, Bot bot) {
        String data = callbackQuery.getData();
        Long telegramUserId = callbackQuery.getFrom().getId();
        UserSession session = bot.getSession(telegramUserId);

        if (data.startsWith("type_")) {
            ContactType type = ContactType.valueOf(data.substring(5));
            session.setPendingType(type);
            showContactList(bot, telegramUserId, type);
            return;
        }

        if ("addContact".equals(data)) {
            session.setState(BotState.WAITING_FOR_CONTACT_NAME);
            bot.sendTextMessage("Введите имя контакта:");
            return;
        }

        if (data.matches("contact\\d+")) {
            Long contactId = Long.parseLong(data.substring(7));
            Optional<Contact> contact = contactService.findById(contactId);
            if (contact.isEmpty()) {
                bot.sendTextMessage("Контакт не найден. Попробуйте /add снова.");
                return;
            }
            session.setSelectedContact(contact.get());
            session.setState(BotState.WAITING_FOR_AMOUNT);
            bot.sendTextMessage("Введите сумму (в рублях):");
        }
    }

    private void showContactList(Bot bot, Long telegramUserId, ContactType type) {
        List<Contact> contacts = contactService.findByTelegramUserIdAndType(telegramUserId, type);
        Map<String, String> buttons = new LinkedHashMap<>();
        for (Contact c : contacts) {
            buttons.put("contact" + c.getId(), c.getName());
        }
        buttons.put("addContact", "Добавить нового");
        bot.sendTextMessage("Выберите контакта или добавьте нового:", buttons);
    }
}
