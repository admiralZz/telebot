package com.admiral.telebot.gpt.port;

import com.admiral.telebot.gpt.GPTPrompt;

import java.util.function.Consumer;

public interface Client {

    String send(GPTPrompt prompt);
}
