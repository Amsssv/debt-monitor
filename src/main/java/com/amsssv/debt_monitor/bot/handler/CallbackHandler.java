package com.amsssv.debt_monitor.bot.handler;

import com.amsssv.debt_monitor.bot.Bot;
import com.amsssv.debt_monitor.bot.BotState;
import com.amsssv.debt_monitor.entity.Lender;
import com.amsssv.debt_monitor.service.LenderService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class CallbackHandler {

  LenderService lenderService;

  public CallbackHandler(LenderService lenderService) {
    this.lenderService = lenderService;
  }

  public void handleCommand(CallbackQuery callbackQuery, Bot bot) {
    String data = callbackQuery.getData();

    if ("addLender".equals(data)) {
      bot.sendTextMessage("""
          Добавьте кредитора. Используйте формат: \
          
          ООО ВТБ\
          
          Владислав\
          
          Игорь Николаевич""");

      bot.state.put(bot.getChatId(), BotState.WAITING_FOR_LENDER_NAME);
    }

    if (data.contains("lender")) {
      bot.sendTextMessage("""
          Добавьте cумму долга. Используйте формат: \
         
         20000\
         
         10000\
         
         50000\
         """);
      bot.state.put(bot.getChatId(), BotState.WAITING_FOR_AMOUNT);
      bot.setSelectedLender(getLenderById(data));
    }
  }

  public Lender getLenderById(String data) {
    Long lenderId = getLenderId(data);
    return lenderService.findById(lenderId);
  }

  public Long getLenderId(String lenderData) {
    Pattern pattern = Pattern.compile("\\d+");
    Matcher matcher = pattern.matcher(lenderData);
    if (matcher.find()) {
      return Long.parseLong(matcher.group());
    }
    return 0L;
  }
}
