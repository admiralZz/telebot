package com.admiral.telebot.http;

import com.admiral.telebot.conf.BotConfig;
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
import java.util.concurrent.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class HttpClient implements Client {
    private static final Logger log = LoggerFactory.getLogger(HttpClient.class);

    private final BotConfig config = BotConfig.getInstance();

    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .callTimeout(60, TimeUnit.SECONDS)
            .readTimeout(60, TimeUnit.SECONDS)
            .writeTimeout(60, TimeUnit.SECONDS)
            .build();

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
                log.debug("FAILED REQUEST\n url: {} \n code: {}\n body: {}\n", config.getGptApiUrl(), code, null);
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
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(config.getGptApiUrl())).newBuilder();

        HttpUrl httpUrl = urlBuilder.build();

        MessageRequest request = new MessageRequest(
                config.getGptModel(),
                Float.parseFloat(config.getGptTemperature()),
                messages);
        String json = Utill.writeResponse(request);

        Request req = reqBuilder.get()
                .url(httpUrl)
                .addHeader(HttpHeaders.CONTENT_TYPE, Constants.APPLICATION_JSON)
                .addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + config.getGptApiToken())
                .post(RequestBody.create(json, MediaType.parse(Constants.APPLICATION_JSON)))
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
