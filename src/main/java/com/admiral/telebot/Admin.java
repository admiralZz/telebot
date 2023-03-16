package com.admiral.telebot;

import java.util.Random;
import java.util.TimerTask;

public class Admin {

    private static final String ON_COMMAND = "/admin";
    private static final String PASSWORD = "Сим сим откройся!";
    private static final int TIME_TO_LIVE = 60_000 * 10; // 10 минут

    private boolean isActivate;
    private boolean isAccessOpen;
    private String chatId = "";
    private CustomTimer timer;
    private Random random = new Random();

    private DataBase dataBase;
    private Bot bot;

    // Набор ответов
    private final String[] answers = {
            "Я вас не понимаю" ,
            "Вы себя хорошо чувствуете?",
            "Вы забыли правила общения",
            "Я тоже хотел бы знать...",
            "Ключевые слова..."};

    // Таймеры
    private final TimerTask tmClosing = new TimerTask() {
        @Override
        public void run() {
            String msg = "Время истекло, я ухожу...";
            bot.sendMsg(chatId, msg);
            closing();
        }
    };

    /**
     * Конструктор
     * @param dataBase - база данных
     */
    public Admin(Bot bot, DataBase dataBase)
    {
        this.bot = bot;
        this.dataBase = dataBase;
    }

    /**
     * Метод приёма сообщений
     * @param message - входящее сообщение
     * @return - исходящие сообщение
     */
    public String onUpdateReceived(String message, String chatId)
    {
        if(message.equals(ON_COMMAND))
        {
            if(!isAdmin(chatId) && !this.chatId.equals("")) {
                String answer = "[ADMIN]: Самозванец!";
                dataBase.insert(chatId, message, answer);
                return answer;
            }

            if(isAccessOpen)
                return "[ADMIN]: Я уже к вашим услугам, не стоит меня звать...";

            isActivate = true;
            Log.log("[ANSWER]: ADMIN CHAT ID=" + chatId);
            this.chatId = chatId;
            return "[ADMIN]: Введите секретное слово...";
        }
        else if(!isAccessOpen)
        {
            if(message.equals(PASSWORD)) {
                isAccessOpen = true;
                timer = new CustomTimer(tmClosing, TIME_TO_LIVE);
                timer.start();
                dataBase.insert(chatId, message, "ВХОД В АДМИНКУ");
                return "[ADMIN]: Рад служить вам Господин";
            }

            isActivate = false;
            this.chatId = "";
            return "[ADMIN]: Ты не посвященный...";
        }

        timer.restart();
        return "[ADMIN]: " + commands(message);
    }

    /**
     * Метод приема команд при активированном администраторе с открытым доступом
     * @param question - входящая команда
     * @return - результат выполнения команды
     */
    private String commands(String question)
    {
        String answer = "";

        switch (question)
        {
            case "/read":
            {
                answer = "\n\n";
                for(String row : dataBase.read())
                    answer += row + "\n\n";
            }
            break;

            case "/close":
            {
                answer = "До встречи Господин...";
                closing();
            }
            break;

            default:
                answer = answers[random.nextInt(answers.length)];
                break;
        }

        return answer;
    }

    private void closing()
    {
        timer.stop();
        isAccessOpen = false;
        isActivate = false;
        chatId = "";
    }

    public boolean isActivate()
    {
        return isActivate;
    }
    public boolean isAdmin(String chatId)
    {
        if(chatId == null)
            return false;

        return this.chatId.equals(chatId);
    }

}
