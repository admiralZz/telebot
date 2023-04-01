package com.admiral.telebot.gpt;

import com.admiral.telebot.conf.BotConfig;
import com.admiral.telebot.gpt.port.Client;
import com.admiral.telebot.http.exception.LimitReachedException;
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


    private boolean isWaitingForAnswer = false;


    public GPTSession(String username, Client client, Consumer<String> putAnswer) {
        this.username = username;
        this.client = client;
        this.putAnswer = putAnswer;
    }

    /**
     * Отправка сообщений с ограничением - необходимостью дождаться ответа
     *
     * */
    public boolean say(String message) {
        log.debug("Waiting for answer {}: {}", username, isWaitingForAnswer);
        if(isWaitingForAnswer) {
            log.debug("{}[QUESTION]: {} is NOT PASSED", username, message);
            putAnswer.accept(Utills.getPreparedUserLimitAnswer());
            return false;
        }
        send(message);

        return true;
    }

    private void send(String message) {
        // Тут мы выстраиваем очередь сообщений у каждой сессии и не блокируем основной поток, чтобы он мог принимать
        // сообщения от других пользователей и создавать их сессии.
        // Сообщения будут отправляться по одному, дожидаясь ответа и затем следующие и т.д.
        isWaitingForAnswer = true;
        log.debug("{}[QUESTION]: {} is PASSED", username, message);
        executorService.submit(() -> {
            prompt.add(GPTMessage.Role.USER, message);
            try {
                gptAnswer(client.send(prompt));
            } catch (LimitReachedException e) {
                isWaitingForAnswer = false;
                putAnswer.accept(Utills.getPreparedGlobalLimitAnswer());
            }
        });
    }

    public void gptAnswer(String message) {
        isWaitingForAnswer = false;
        log.debug("{}[ANSWER]: {}", username, message);
        prompt.add(GPTMessage.Role.ASSISTANT, message);
        // Callback
        putAnswer.accept(message);
    }
}
