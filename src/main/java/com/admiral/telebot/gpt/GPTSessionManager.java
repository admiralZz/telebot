package com.admiral.telebot.gpt;

import com.admiral.telebot.gpt.port.Client;
import com.admiral.telebot.http.HttpClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.BiConsumer;

public class GPTSessionManager {
    private static final Logger log = LoggerFactory.getLogger(GPTSessionManager.class);
    private final Map<Long, GPTSession> sessions = new HashMap<>();
    private final ExecutorService executorService = Executors.newWorkStealingPool();
    private final Client client;
    private final BiConsumer<Long, String> getAnswer;

    public GPTSessionManager(Client client, BiConsumer<Long, String> getAnswer) {
        this.client = client;
        this.getAnswer = getAnswer;
    }

    public void chat(Long chatId, String username, String message) {
        GPTSession session = sessions
                // создать сессию если отсутствует
                .computeIfAbsent(chatId, id -> {
                    GPTSession s = new GPTSession(
                            username,
                            new HttpClient(),
                            // ответит послать в callback
                            answer -> getAnswer.accept(chatId, answer)
                    );
                    log.debug("Created a new session for user {} chatId {}", username, chatId);
                    return s;
                });
        executorService.submit(() -> session.say(message));
    }
}
