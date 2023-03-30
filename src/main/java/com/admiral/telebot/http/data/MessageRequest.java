package com.admiral.telebot.http.data;

import com.admiral.telebot.http.data.common.Message;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class MessageRequest {
    @JsonProperty("model")
    private final String model;
    @JsonProperty("temperature")
    private final Float temperature;
    @JsonProperty("messages")
    private final List<Message> messages;

    public MessageRequest(String model, Float temperature, List<Message> messages) {
        this.model = model;
        this.temperature = temperature;
        this.messages = messages;
    }

    @Override
    public String toString() {
        return "MessageRequest{" +
                "model='" + model + '\'' +
                ", temperature='" + temperature + '\'' +
                ", messages=" + messages +
                '}';
    }
}
