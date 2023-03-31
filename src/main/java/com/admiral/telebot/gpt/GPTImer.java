package com.admiral.telebot.gpt;

import java.util.Timer;

public class GPTImer extends Timer {

    boolean isPossibleToRun() {
        return false;
    }
}
