package com.admiral.telebot.gpt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class GPTPrompt {
    private static final Logger log = LoggerFactory.getLogger(GPTPrompt.class);
    private final List<GPTMessage> messages = new ArrayList<>();
    private static final Integer CAPACITY = 15;

    /** @param role - Начальная установка для ИИ, в какой роли ему быть
     * Например: You are a helpful assistant.
     * */
    public GPTPrompt(String role) {
        messages.add(new GPTMessage(GPTMessage.Role.SYSTEM, role));
    }

    public void add(GPTMessage.Role role, String content) {
        messages.add(new GPTMessage(role, content));
        controlPromptSize();
    }

    public List<GPTMessage> getMessages() {
        return messages;
    }

    private void controlPromptSize() {
        log.debug("Size of prompt: {}", messages.size());
        if(messages.size() > CAPACITY) {
            GPTMessage message = messages.remove(1);
            log.debug("Removed from prompt: {}", message);
        }
    }
}
