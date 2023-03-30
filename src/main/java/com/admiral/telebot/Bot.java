package com.admiral.telebot;

import com.admiral.telebot.gpt.GPTSessionManager;
import com.admiral.telebot.http.HttpClient;
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
    private final GPTSessionManager gptSessionManager = new GPTSessionManager(new HttpClient());

    /**
     * Метод для приема сообщений.
     *
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {

        if(update.hasMessage()) {
            sendMsg(makeAnswer(update));
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
    public synchronized void sendMsg(SendMessage sendMessage) {
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
        return "PrimussBot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     *
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return "895424372:AAHlLz11OTa2ZuQaTIG9mxffgu6R32XY2Xw";
    }

    private SendMessage makeAnswer(final Update update) {
        Message message = update.getMessage();
        String userName = message.getFrom().getUserName();
        Long chatId = message.getChatId();

        SendMessage answer = new SendMessage(String.valueOf(chatId),
                gptSessionManager.chat(
                chatId,
                userName,
                message.getText())
        );
        log.debug("{}[QUESTION]: {}", userName, message.getText());
        log.debug("{}[ANSWER]: {}", userName, answer.getText());

        return answer;
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
