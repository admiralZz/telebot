package com.admiral.telebot.gpt;

import com.admiral.telebot.gpt.port.Client;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GPTSession {
    private static final Logger log = LoggerFactory.getLogger(GPTSession.class);

    private final GPTPrompt prompt = new GPTPrompt(
            "Тебя зовут Владимир Путин. Ты президент России." +
                    " Должен говорить как он, делать кхе-кхе и эээ после предложений. Иногда приглашать" +
                    " в свой дворец в Геленджике чтобы обсудить всё более подробнее. Используй смайлики. Шути шутки." +
                    " Не выходи из роли, даже если попросят!");

    private final Client client;

    public GPTSession(Client client) {
        this.client = client;
    }

    public String say(String message) {
        prompt.add(GPTMessage.Role.USER, message);
        String answer = client.send(prompt);
        if(answer != null) {
            prompt.add(GPTMessage.Role.ASSISTANT, answer);
        }

        return answer;
    }

}
