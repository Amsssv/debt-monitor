package com.amsssv.debt_monitor.bot.handler;

import com.amsssv.debt_monitor.bot.Bot;
import com.amsssv.debt_monitor.bot.BotState;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;


@Component
public class CallbackHandler {


  public CallbackHandler() {

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
  }
}
