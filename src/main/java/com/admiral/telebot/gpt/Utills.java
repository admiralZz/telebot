package com.admiral.telebot.gpt;

import com.admiral.telebot.conf.BotConfig;

import java.util.Random;

public class Utills {
    private static final Random random = new Random();

    public static int nextInt(int i) {
        return random.nextInt(i);
    }

    public static String getPreparedUserLimitAnswer() {
        return BotConfig.preparedUserLimitAnswers[Utills.nextInt(BotConfig.preparedUserLimitAnswers.length)];
    }

    public static String getPreparedGlobalLimitAnswer() {
        return BotConfig.preparedGlobalLimitAnswers[Utills.nextInt(BotConfig.preparedGlobalLimitAnswers.length)];
    }
}
