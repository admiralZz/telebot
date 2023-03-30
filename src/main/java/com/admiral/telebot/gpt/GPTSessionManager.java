package com.admiral.telebot.gpt;

import com.admiral.telebot.gpt.port.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

public class GPTSessionManager {
    private static final Logger log = LoggerFactory.getLogger(GPTSessionManager.class);
    private final Map<Long, GPTSession> sessions = new HashMap<>();

    private final Client client;

    public GPTSessionManager(Client client) {
        this.client = client;
    }

    public String chat(Long chatId, String username, String message) {
        return sessions
                // создать если отсутствует
                .computeIfAbsent(chatId, id -> {
                    GPTSession session = new GPTSession(client);
                    log.debug("Created a new session for user {} chatId {}", username, chatId);
                    return session;
                })
                .say(message);
    }
}
