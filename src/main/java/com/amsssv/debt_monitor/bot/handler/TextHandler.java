package com.amsssv.debt_monitor.bot.handler;

import com.amsssv.debt_monitor.bot.Bot;
import com.amsssv.debt_monitor.bot.BotState;
import com.amsssv.debt_monitor.bot.UserSession;
import com.amsssv.debt_monitor.entity.Contact;
import com.amsssv.debt_monitor.service.ContactService;
import com.amsssv.debt_monitor.service.DebtService;
import com.amsssv.debt_monitor.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class TextHandler {

    private final TelegramUserService telegramUserService;
    private final ContactService contactService;
    private final DebtService debtService;

    public void handleCommand(Message message, Bot bot) {
        Long telegramUserId = message.getFrom().getId();
        String text = message.getText();
        UserSession session = bot.getSession(telegramUserId);

        if (session.getState() == BotState.WAITING_FOR_CONTACT_NAME) {
            Optional<Contact> existing = contactService.findByNameAndTelegramUserId(text, telegramUserId);
            if (existing.isPresent()) {
                bot.sendTextMessage("Контакт с именем «" + text + "» уже существует");
            } else {
                Contact newContact = telegramUserService.addContact(text, session.getPendingType(), telegramUserId);
                session.setSelectedContact(newContact);
                session.setState(BotState.WAITING_FOR_AMOUNT);
                bot.sendTextMessage("Введите сумму (в рублях):");
            }
            return;
        }

        if (session.getState() == BotState.WAITING_FOR_AMOUNT) {
            try {
                int amount = Integer.parseInt(text.trim());
                Contact contact = session.getSelectedContact();
                debtService.save(amount, contact);
                session.setState(BotState.IDLE);
                bot.sendTextMessage("Добавлено: " + contact.getName() + " — " + amount + " ₽");
            } catch (NumberFormatException e) {
                bot.sendTextMessage("Введите целое число, например: 5000");
            }
        }
    }
}
