package com.admiral.telebot.gpt;

import java.util.Random;

public class Utills {
    private static final Random random = new Random();

    public static int nextInt(int i) {
        return random.nextInt(i);
    }
}
