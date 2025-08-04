package com.amsssv.debt_monitor.bot;

import com.amsssv.debt_monitor.bot.handler.CallbackHandler;
import com.amsssv.debt_monitor.bot.handler.CommandHandler;
import com.amsssv.debt_monitor.bot.handler.TextHandler;
import com.amsssv.debt_monitor.entity.Lender;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class Bot extends TelegramLongPollingBot {

  public final CommandHandler commandHandler;
  private final CallbackHandler callbackHandler;
  private final TextHandler textHandler;
  private final ThreadLocal<Update> updateEvent = new ThreadLocal<>();
  public final Map<Long,BotState> state = new HashMap<>();
  @Getter
  @Setter
  public Lender selectedLender;

  @Value(value = "${bot.username}")
  private String botUsername;

  public Bot(@Value(value = "${bot.token}") String token, CommandHandler commandHandler, CallbackHandler callbackHandler, TextHandler textHandler) {
    super(token);
    this.commandHandler = commandHandler;
    this.callbackHandler = callbackHandler;
    this.textHandler = textHandler;
  }

  @Override
  public void onUpdateReceived(Update update) {
    this.updateEvent.set(update);

    if (update.hasCallbackQuery()) {
      CallbackQuery callbackQuery = update.getCallbackQuery() ;

      callbackHandler.handleCommand(callbackQuery, this);
    }

    if (update.hasMessage()) {
      Message message = update.getMessage();

      if (message.isCommand()) {
        commandHandler.handleCommand(message, this);
      } else {
        textHandler.handleCommand(message, this);
      }
    }
  }

  @Override
  public String getBotUsername() {
    return botUsername;
  }

  public Long getChatId() {
    if (updateEvent.get().hasMessage()) {
      return updateEvent.get().getMessage().getChatId();
    }
    if (updateEvent.get().hasCallbackQuery()) {
      return updateEvent.get().getCallbackQuery().getMessage().getChatId();
    }
    return null;
  }

  public SendMessage createMessage(String text ) {
    Long chatId = getChatId();
    SendMessage newMessage = SendMessage.builder().chatId(chatId).text(text).build();
    newMessage.enableHtml(true);
    return newMessage;
  }

  public void sendTextMessage(String text) {
    SendMessage newMessage = createMessage(text);
    try {
      execute(newMessage);
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  public void sendTextMessage(String text, Map<String, String> buttons) {
    SendMessage newMessage = createMessage(text);

    if (buttons != null && !buttons.isEmpty()) {
      InlineKeyboardMarkup markup = createInlineKeyboardMarkup(buttons);
      newMessage.setReplyMarkup(markup);
    }

    try {
      execute(newMessage);
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }

  public InlineKeyboardButton createButton(String text , String callbackData) {
    return InlineKeyboardButton.builder().text(text).callbackData(callbackData).build();
  }

  public InlineKeyboardMarkup createInlineKeyboardMarkup(Map<String, String> buttons) {
    InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    List<InlineKeyboardButton> row = new ArrayList<>();
    List<List<InlineKeyboardButton>> rows = new ArrayList<>();


    for (String key : buttons.keySet()) {
      InlineKeyboardButton button = createButton(buttons.get(key), key);
      row.add(button);

      int COLUMNS = 2;
      if (row.size() == COLUMNS) {
        rows.add(row);
        row = new ArrayList<>();
      }
    }

    if (!row.isEmpty()) {
      rows.add(row);
    }

    inlineKeyboardMarkup.setKeyboard(rows);

    return inlineKeyboardMarkup;
  }
}
