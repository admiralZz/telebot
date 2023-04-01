package com.admiral.telebot.gpt;

import com.admiral.telebot.conf.BotConfig;
import com.admiral.telebot.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiConsumer;

public class GPTSessionManager {
    private static final Logger log = LoggerFactory.getLogger(GPTSessionManager.class);

    private final Map<Long, GPTSession> sessions = new ConcurrentHashMap<>();

    private final BiConsumer<Long, String> getAnswer;

    private final HttpClient client = new HttpClient();

    private final GPTMessageCounter counter = new GPTMessageCounter();

    public GPTSessionManager(BiConsumer<Long, String> getAnswer) {
        this.getAnswer = getAnswer;
    }

    public void chat(Long chatId, String username, String message) {
        sessions
                // создать сессию если отсутствует
                .computeIfAbsent(chatId, id -> {
                    GPTSession s = new GPTSession(
                            username,
                            client,
                            // ответит послать в callback
                            answer -> getAnswer.accept(chatId, answer)
                    );
                    log.debug("Created a new session for user {} chatId {}", username, chatId);
                    return s;
                }).say(message);

    }

    /**
     * Чат с контролем сообщений в минуту
     * */
    public void chatWithControl(Long chatId, String username, String message) {
        if(counter.tooManyMessage()) {
            getAnswer.accept(chatId,
                    Utills.getPreparedGlobalLimitAnswer());
            return;
        }

        boolean isMessageSend = sessions
                // создать сессию если отсутствует
                .computeIfAbsent(chatId, id -> {
                    GPTSession s = new GPTSession(
                            username,
                            client,
                            // ответит послать в callback
                            answer -> getAnswer.accept(chatId, answer)
                    );
                    log.debug("Created a new session for user {} chatId {}", username, chatId);
                    return s;
                }).say(message);

        if(isMessageSend) {
            counter.countMessage();
        }
    }
}
