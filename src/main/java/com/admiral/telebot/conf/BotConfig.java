package com.admiral.telebot.conf;

import java.io.FileReader;
import java.io.IOException;
import java.util.Objects;
import java.util.Properties;

public class BotConfig {
    private static final String ROOT_PATH = Objects.requireNonNull(Thread.currentThread()
                    .getContextClassLoader()
                    .getResource("")).getPath();
    private static final String GPT_PROPERTIES = ROOT_PATH + "bot.properties";

    private static final BotConfig instance = new BotConfig();
    private final Properties properties = new Properties();

    public static final String[] preparedUserLimitAnswers = {
            "Пожалуйста, дождитесь ответа",
            "Дайте время...",
            "Не так быстро" ,
            "Будьте последовательны"
    };

    public static final String[] preparedGlobalLimitAnswers = {
            "Слишком много сообщений в данный момент..",
            "Попробуйте попозже, очень много пользователей",
            "Я сильно занят, многим нужно ответить" ,
    };

    private BotConfig() {
        try (FileReader reader = new FileReader(GPT_PROPERTIES)){
            properties.load(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        System.getenv().forEach(properties::setProperty);
    }

    public String getGptApiUrl() {
        return properties.getProperty("gpt.url");
    }

    public String getGptModel() {
        return properties.getProperty("gpt.model");
    }

    public String getGptRole() {
        return properties.getProperty("gpt.role");
    }

    public String getGptTemperature() {
        return properties.getProperty("gpt.temperature");
    }

    public String getGptApiToken() {
        return properties.getProperty("gpt.token");
    }

    public String getTelegramBotApiToken() {
        return properties.getProperty("telegram.token");
    }

    public String getTelegramBotUsername() {
        return properties.getProperty("telegram.username");
    }

    public static BotConfig getInstance() {
        return instance;
    }
}
