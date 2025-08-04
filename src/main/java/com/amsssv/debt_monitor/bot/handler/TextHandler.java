package com.amsssv.debt_monitor.bot.handler;

import com.amsssv.debt_monitor.bot.Bot;
import com.amsssv.debt_monitor.bot.BotState;
import com.amsssv.debt_monitor.entity.Lender;
import com.amsssv.debt_monitor.service.DebtService;
import com.amsssv.debt_monitor.service.LenderService;
import com.amsssv.debt_monitor.service.TelegramUserService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Component
public class TextHandler {

  private final TelegramUserService telegramUserService;
  private final LenderService lenderService;
  private final DebtService debtService;

  public TextHandler(TelegramUserService telegramUserService, LenderService lenderService,  DebtService debtService) {
    this.telegramUserService = telegramUserService;
    this.lenderService = lenderService;
    this.debtService = debtService;
  }

  public void handleCommand(Message message, Bot bot) {
    Long userId = message.getFrom().getId();
    String text = message.getText();

    if (bot.state.get(userId) == BotState.WAITING_FOR_LENDER_NAME) {
      Optional<Lender> lender = lenderService.findByLenderName(text);
      if (lender.isPresent()) {
        bot.sendTextMessage("Lender with this name " + text + " already exist");
      } else {
        addLender(text, userId);
        bot.sendTextMessage("Lender " + text + " has been added");
        bot.state.put(userId, BotState.IDLE);
      }
    }
    if (bot.state.get(userId) == BotState.WAITING_FOR_AMOUNT) {
      Integer amount = Integer.parseInt(text);
      Lender lender = bot.getSelectedLender();
      addDebt(amount, lender);
      bot.sendTextMessage("Ваш долг " + lender.getName() + ": " + amount);
      bot.state.put(userId, BotState.IDLE);
    }
  }

  public void addLender(String lenderName, Long userId) {
    telegramUserService.addLender(lenderName, userId);
  }

  public void addDebt(Integer amount, Lender lender) {
    debtService.save(amount, lender);
  }
}
