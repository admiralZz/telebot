package com.admiral.telebot.gpt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public class GPTMessageCounter {
    private static final Logger log = LoggerFactory.getLogger(GPTMessageCounter.class);
    private int count = 0;
    private final Timer timer = new Timer();
    private boolean isTimerStarted = false;

    public void countMessage() {
        log.debug("For 1 min the message count: {}", ++count);
        if(isTimerStarted) {
            return;
        }
        isTimerStarted = true;
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                count = 0;
                isTimerStarted = false;
                log.debug("Reset the count and timer");
            }
        };
        timer.schedule(task, 60000);
    }

    public boolean tooManyMessage() {
        return count >= 26;
    }
}
