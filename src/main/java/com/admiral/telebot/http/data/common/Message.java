package com.admiral.telebot.http.data.common;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Message {
    @JsonProperty("role")
    private String role;
    @JsonProperty("content")
    private String content;

    public Message() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "Message{" +
                "role=" + role +
                ", content='" + content + '\'' +
                '}';
    }
}
