package com.amsssv.debt_monitor.bot;

import com.amsssv.debt_monitor.bot.handler.CallbackHandler;
import com.amsssv.debt_monitor.bot.handler.CommandHandler;
import com.amsssv.debt_monitor.bot.handler.TextHandler;
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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class Bot extends TelegramLongPollingBot {

    private final CommandHandler commandHandler;
    private final CallbackHandler callbackHandler;
    private final TextHandler textHandler;
    private final ThreadLocal<Update> updateEvent = new ThreadLocal<>();
    private final Map<Long, UserSession> sessions = new ConcurrentHashMap<>();

    @Value("${bot.username}")
    private String botUsername;

    public Bot(@Value("${bot.token}") String token,
               CommandHandler commandHandler,
               CallbackHandler callbackHandler,
               TextHandler textHandler) {
        super(token);
        this.commandHandler = commandHandler;
        this.callbackHandler = callbackHandler;
        this.textHandler = textHandler;
    }

    @Override
    public void onUpdateReceived(Update update) {
        this.updateEvent.set(update);
        if (update.hasCallbackQuery()) {
            callbackHandler.handleCommand(update.getCallbackQuery(), this);
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

    public UserSession getSession(Long userId) {
        return sessions.computeIfAbsent(userId, k -> new UserSession());
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

    public void sendTextMessage(String text) {
        try {
            execute(buildMessage(text, null));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendTextMessage(String text, Map<String, String> buttons) {
        try {
            execute(buildMessage(text, buttons));
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

    private SendMessage buildMessage(String text, Map<String, String> buttons) {
        SendMessage message = SendMessage.builder().chatId(getChatId()).text(text).build();
        message.enableHtml(true);
        if (buttons != null && !buttons.isEmpty()) {
            message.setReplyMarkup(buildKeyboard(buttons));
        }
        return message;
    }

    private InlineKeyboardMarkup buildKeyboard(Map<String, String> buttons) {
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        for (Map.Entry<String, String> entry : buttons.entrySet()) {
            row.add(InlineKeyboardButton.builder()
                    .text(entry.getValue())
                    .callbackData(entry.getKey())
                    .build());
            if (row.size() == 2) {
                rows.add(new ArrayList<>(row));
                row.clear();
            }
        }
        if (!row.isEmpty()) {
            rows.add(row);
        }
        InlineKeyboardMarkup markup = new InlineKeyboardMarkup();
        markup.setKeyboard(rows);
        return markup;
    }
}
