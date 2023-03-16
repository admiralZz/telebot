package com.admiral.telebot;

import java.util.TimerTask;

public class CustomTimer {

    private Thread thread;
    private TimerTask timerTask;
    private long delay;

    public CustomTimer(TimerTask task, long delay)
    {
        this.timerTask = task;
        this.delay = delay;
    }
    private void update()
    {
        this.thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                    timerTask.run();
                } catch (InterruptedException e) {
                }
            }
        });
    }

    public void start()
    {
        update();
        thread.start();
    }
    public void stop()
    {
        thread.interrupt();
        update();
    }
    public void restart()
    {
        stop();
        start();
    }

}
