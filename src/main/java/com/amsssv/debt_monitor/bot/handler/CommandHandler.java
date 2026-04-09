package com.amsssv.debt_monitor.bot.handler;

import com.amsssv.debt_monitor.bot.Bot;
import com.amsssv.debt_monitor.entity.Contact;
import com.amsssv.debt_monitor.entity.ContactType;
import com.amsssv.debt_monitor.entity.TelegramUser;
import com.amsssv.debt_monitor.service.ContactService;
import com.amsssv.debt_monitor.service.TelegramUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
@RequiredArgsConstructor
public class CommandHandler {

    private final TelegramUserService telegramUserService;
    private final ContactService contactService;

    public void handleCommand(Message message, Bot bot) {
        String command = message.getText();
        Long telegramUserId = message.getFrom().getId();
        Optional<TelegramUser> userOpt = telegramUserService.findByTelegramUserId(telegramUserId);

        if ("/start".equals(command)) {
            if (userOpt.isPresent()) {
                bot.sendTextMessage("Вы уже зарегистрированы. Используйте /add или /list.");
            } else {
                String firstName = message.getFrom().getFirstName();
                telegramUserService.save(
                        telegramUserId,
                        message.getFrom().getUserName(),
                        firstName,
                        message.getFrom().getLastName(),
                        message.getChatId()
                );
                bot.sendTextMessage("Добро пожаловать, " + firstName + "! 👋\nИспользуйте /add чтобы добавить долг, /list чтобы посмотреть список.");
            }
            return;
        }

        if (userOpt.isEmpty()) {
            bot.sendTextMessage("Сначала введите /start");
            return;
        }

        if ("/add".equals(command)) {
            Map<String, String> buttons = new LinkedHashMap<>();
            buttons.put("type_CREDITOR", "Я должен (кредитор)");
            buttons.put("type_DEBTOR", "Мне должны (должник)");
            bot.sendTextMessage("Кто берёт в долг?", buttons);
        }

        if ("/list".equals(command)) {
            List<Contact> contacts = contactService.findWithDebtsByTelegramUserId(telegramUserId);

            List<Contact> creditors = contacts.stream()
                    .filter(c -> c.getType() == ContactType.CREDITOR && !c.getDebts().isEmpty())
                    .toList();
            List<Contact> debtors = contacts.stream()
                    .filter(c -> c.getType() == ContactType.DEBTOR && !c.getDebts().isEmpty())
                    .toList();

            if (creditors.isEmpty() && debtors.isEmpty()) {
                bot.sendTextMessage("У вас пока нет записей. Используйте /add чтобы добавить.");
                return;
            }

            StringBuilder sb = new StringBuilder("📋 Ваши долги:\n");

            if (!creditors.isEmpty()) {
                sb.append("\n💳 Я должен:\n");
                int total = 0;
                for (Contact c : creditors) {
                    int sum = c.getDebts().stream().mapToInt(d -> d.getAmount()).sum();
                    total += sum;
                    sb.append("• ").append(c.getName()).append(" — ").append(sum).append(" ₽\n");
                }
                sb.append("Итого: ").append(total).append(" ₽\n");
            }

            if (!debtors.isEmpty()) {
                sb.append("\n💰 Мне должны:\n");
                int total = 0;
                for (Contact c : debtors) {
                    int sum = c.getDebts().stream().mapToInt(d -> d.getAmount()).sum();
                    total += sum;
                    sb.append("• ").append(c.getName()).append(" — ").append(sum).append(" ₽\n");
                }
                sb.append("Итого: ").append(total).append(" ₽\n");
            }

            bot.sendTextMessage(sb.toString());
        }
    }
}
