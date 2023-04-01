package com.admiral.telebot.gpt.port;

import com.admiral.telebot.gpt.GPTPrompt;
import com.admiral.telebot.http.exception.LimitReachedException;

import java.util.function.Consumer;

public interface Client {

    String send(GPTPrompt prompt) throws LimitReachedException;
}
