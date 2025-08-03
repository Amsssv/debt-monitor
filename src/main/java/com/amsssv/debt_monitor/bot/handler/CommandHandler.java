package com.amsssv.debt_monitor.bot.handler;

import com.amsssv.debt_monitor.bot.Bot;
import com.amsssv.debt_monitor.entity.Lender;
import com.amsssv.debt_monitor.entity.TelegramUser;
import com.amsssv.debt_monitor.service.LenderService;
import com.amsssv.debt_monitor.service.TelegramUserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CommandHandler {
  private final TelegramUserService telegramUserService;
  private final LenderService lenderService;

  public CommandHandler(TelegramUserService telegramUserService,  LenderService lenderService ) {
    this.telegramUserService = telegramUserService;
    this.lenderService = lenderService;
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
        bot.sendTextMessage("This user already exist");
      } else {
        saveUser(userId, userName, firstName, lastName, chatId);
        bot.sendTextMessage("Welcome" + userName);
      }
    }

    if (command.equals("/lenderList")) {
      Map<String, String> buttons = new HashMap<>();
      List<Lender>  lenders = getAllLenders();
      for (Lender lender : lenders) {
        String lenderKey  = "lender" +  lender.getId();
        buttons.put(lenderKey, lender.getName());
      }
      buttons.put("addLender", "Добавить");

      bot.sendTextMessage("Выберите кредитора, либо добавьте нового", buttons);
    }
  }

  public void saveUser(Long userId, String userName, String firstName, String lastName, Long chatId) {
    telegramUserService.save(
        userId,
        userName,
        firstName,
        lastName,
        chatId
    );
  }

  public List<Lender> getAllLenders() {
    return lenderService.findAll();
  }
}
