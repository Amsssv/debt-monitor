package com.amsssv.debt_monitor.bot;


import com.amsssv.debt_monitor.bot.handler.CommandHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class Bot extends TelegramLongPollingBot {

  public final CommandHandler commandHandler;

  @Value(value = "${bot.username}")
  private String botUsername;

  public Bot (@Value(value = "${bot.token}")String token, CommandHandler commandHandler) {
    super(token);
    this.commandHandler = commandHandler;
  }
  @Override
  public void onUpdateReceived(Update update) {
    Message message = update.getMessage();

    if (message.isCommand()) {
      commandHandler.handleCommand(message, this);
    }
  }

  @Override
  public String getBotUsername() {
    return botUsername;
  }

  public void sendMessage(String text, Long chatId) {
    SendMessage newMessage = SendMessage.builder().chatId(chatId).text(text).build();
    newMessage.enableHtml(true);

    try {
      execute(newMessage);
    } catch (TelegramApiException e) {
      throw new RuntimeException(e);
    }
  }
}
