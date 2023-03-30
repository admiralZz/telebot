package com.admiral.telebot.gpt;

import com.admiral.telebot.conf.BotConfig;
import com.admiral.telebot.gpt.port.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Consumer;

public class GPTSession {
    private static final Logger log = LoggerFactory.getLogger(GPTSession.class);
    private final BotConfig config = BotConfig.getInstance();
    private final GPTPrompt prompt = new GPTPrompt(config.getGptRole());
    private final String username;
    private final Client client;
    private final Consumer<String> putAnswer;

    public GPTSession(String username, Client client, Consumer<String> putAnswer) {
        this.username = username;
        this.client = client;
        this.putAnswer = putAnswer;
    }

    public synchronized void say(String message) {
        log.debug("{}[QUESTION]: {}", username, message);
        prompt.add(GPTMessage.Role.USER, message);
        answer(client.send(prompt));
    }

    public void answer(String message) {
        log.debug("{}[ANSWER]: {}", username, message);
        prompt.add(GPTMessage.Role.ASSISTANT, message);
        putAnswer.accept(message);
    }

}
