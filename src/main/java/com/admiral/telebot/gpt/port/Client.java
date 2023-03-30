package com.admiral.telebot.gpt.port;

import com.admiral.telebot.gpt.GPTPrompt;

public interface Client {

    String send(String message);

    String send(GPTPrompt prompt);
}
