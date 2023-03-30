package com.admiral.telebot.http;

import com.admiral.telebot.gpt.GPTMessage;
import com.admiral.telebot.gpt.GPTPrompt;
import com.admiral.telebot.gpt.port.Client;
import com.admiral.telebot.http.data.MessageRequest;
import com.admiral.telebot.http.data.MessageResponse;
import com.admiral.telebot.http.data.common.Message;
import okhttp3.*;
import org.apache.http.HttpHeaders;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class HttpClient implements Client {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    private static final String URL = "https://api.openai.com/v1/chat/completions";
    private static final String APPLICATION_JSON = "application/json";
    private static final String MODEL = "gpt-3.5-turbo";
    private static final Float TEMPERATURE = 0.7f; // 0.0 to 2.0

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

    @Override
    public String send(String message) {
        return processTo(List.of(createMessage(GPTMessage.Role.USER.getValue(), message)));
    }

    @Override
    public String send(GPTPrompt prompt) {
        return processTo(convertFrom(prompt.getMessages()));
    }

    private String processTo(List<Message> messages) {
        try(Response response = postCall(messages).execute()) {
            int code = response.code();
            ResponseBody body = response.body();
            MessageResponse messageResponse = null;

            if(body != null) {
                String content = body.string();
                messageResponse = Utill.readRequest(content, MessageResponse.class);
                body.close();
            } else {
                log.debug("FAILED REQUEST\n url: {} \n code: {}\n body: {}\n", URL, code, null);
            }

            log.debug("Received the response: {} {}", code, Utill.prettyJson(messageResponse));
            return messageResponse == null || messageResponse.getChoices() == null ? "" :
                    messageResponse.getChoices().stream()
                            .map(choicesItem -> choicesItem.getMessage().getContent())
                            .collect(Collectors.joining(System.lineSeparator()));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private Call postCall(List<Message> messages) {
        Request.Builder reqBuilder = new Request.Builder();
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(URL)).newBuilder();

        HttpUrl httpUrl = urlBuilder.build();

        MessageRequest request = new MessageRequest(
                MODEL,
                TEMPERATURE,
                messages);
        String json = Utill.writeResponse(request);

        Request req = reqBuilder.get()
                .url(httpUrl)
                .addHeader(HttpHeaders.CONTENT_TYPE, APPLICATION_JSON)
                .addHeader(HttpHeaders.AUTHORIZATION, TOKEN)
                .post(RequestBody.create(json, MediaType.parse(APPLICATION_JSON)))
                .build();
        log.debug("POST {} request: {}", httpUrl.url(), Utill.prettyJson(request));

        return client.newCall(req);
    }

    private Message createMessage(String role, String content) {
        Message message = new Message();
        message.setRole(role);
        message.setContent(content);

        return message;
    }

    private List<Message> convertFrom(List<GPTMessage> gptMessages) {
        return gptMessages.stream()
                .map(gptMessage -> createMessage(gptMessage.getRole().getValue(), gptMessage.getMessage()))
                .collect(Collectors.toList());
    }

}
