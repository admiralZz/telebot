package com.admiral.telebot;

import com.admiral.telebot.conf.BotConfig;
import com.admiral.telebot.gpt.GPTSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.LongPollingBot;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

import java.util.List;

public class Bot extends TelegramLongPollingBot {

    private static final Logger log = LoggerFactory.getLogger(Bot.class);
    private final BotConfig config = BotConfig.getInstance();
    private final GPTSessionManager gptSessionManager = new GPTSessionManager(
            (chatId,message) -> sendMsg(new SendMessage(String.valueOf(chatId), message))
    );

    /**
     * Метод для приема сообщений.
     *
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage()) {
            handleUserMessage(update);
        }
    }

    @Override
    public void onUpdatesReceived(List<Update> updates) {
        super.onUpdatesReceived(updates);
    }

    /**
     * Метод для настройки сообщения и его отправки.
     *
     * @param sendMessage отправляемое сообщение.
     */
    public void sendMsg(SendMessage sendMessage) {
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.debug("Exception: {}", e.toString());
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     *
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return config.getTelegramBotUsername();
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return config.getTelegramBotApiToken();
    }

    private void handleUserMessage(final Update update) {
        Message message = update.getMessage();
        String userName = message.getFrom().getUserName();
        Long chatId = message.getChatId();

        gptSessionManager.chat(
                chatId,
                userName,
                message.getText());
    }

    public static void main(String[] args) {
        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(Bot.getBot());
            log.info("Bot is started...");
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }


    private static LongPollingBot getBot() {
        return new Bot();
    }
}
