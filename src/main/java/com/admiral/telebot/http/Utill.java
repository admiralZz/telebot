package com.admiral.telebot.http;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class Utill {

    private static final ObjectMapper mapper = new ObjectMapper();

    static {
        mapper.writerWithDefaultPrettyPrinter();
    }
    public static  <T> T readRequest(String content, Class<T> tClass) {
        try {
            return mapper.readValue(content, tClass);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String writeResponse(T response) {
        try {
            return mapper.writeValueAsString(response);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    public static <T> String prettyJson(T body) {
        try {
            return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(body);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }


}
