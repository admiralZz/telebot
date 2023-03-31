package com.admiral.telebot.gpt;

import com.admiral.telebot.conf.BotConfig;
import com.admiral.telebot.gpt.port.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedTransferQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

public class GPTSession {
    private static final Logger log = LoggerFactory.getLogger(GPTSession.class);
    private final BotConfig config = BotConfig.getInstance();
    private final GPTPrompt prompt = new GPTPrompt(config.getGptRole());
    private final String username;
    private final Client client;
    private final Consumer<String> putAnswer;
    private final ExecutorService executorService = new ThreadPoolExecutor(1, 1, 0L, TimeUnit.MILLISECONDS,
            new LinkedTransferQueue<>());

    public GPTSession(String username, Client client, Consumer<String> putAnswer) {
        this.username = username;
        this.client = client;
        this.putAnswer = putAnswer;
    }

    public void say(String message) {
        log.debug("{}[QUESTION]: {}", username, message);
        // Тут мы выстраиваем очередь сообщений у каждой сессии и не блокируем основной поток, чтобы он мог принимать
        // сообщения от других пользователей и создавать их сессии.
        // Сообщения будут отправляться по одному, дожидаясь ответа и затем следующие и т.д.
        executorService.submit(() -> {
            prompt.add(GPTMessage.Role.USER, message);
            answer(client.send(prompt));
        });
    }

    public void answer(String message) {
        log.debug("{}[ANSWER]: {}", username, message);
        prompt.add(GPTMessage.Role.ASSISTANT, message);
        putAnswer.accept(message);
    }
}
