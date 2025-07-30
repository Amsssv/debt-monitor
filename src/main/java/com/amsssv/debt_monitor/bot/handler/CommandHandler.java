package com.amsssv.debt_monitor.bot.handler;

import com.amsssv.debt_monitor.bot.Bot;
import com.amsssv.debt_monitor.entity.TelegramUser;
import com.amsssv.debt_monitor.service.TelegramUserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Component
public class CommandHandler {
  private final TelegramUserService telegramUserService;

  public CommandHandler(TelegramUserService telegramUserService) {
    this.telegramUserService = telegramUserService;
  }

  public void handleCommand(Message message, Bot bot) {
    String command = message.getText();
    Long chatId = message.getChatId();

    Long userId = message.getFrom().getId();
    String userName = message.getFrom().getUserName();
    String firstName = message.getFrom().getFirstName();
    String lastName = message.getFrom().getLastName();

    if (command.equals("/start")) {
      Optional<TelegramUser> telegramUser = telegramUserService.findByTelegramUserId(userId);
      if (telegramUser.isPresent()) {
        bot.sendMessage("This user already exist", chatId);
      } else {
        saveUser(userId, userName, firstName, lastName, chatId);
        bot.sendMessage("Welcome" + userName, chatId);
      }
    }
  }

  public void saveUser(Long userId, String userName, String firstName, String lastName, Long chatId) {
    telegramUserService.saveOrUpdate(
        userId,
        userName,
        firstName,
        lastName,
        chatId
    );
  }
}
