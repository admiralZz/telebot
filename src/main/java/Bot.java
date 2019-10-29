import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import org.telegram.telegrambots.generics.LongPollingBot;

import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Bot extends TelegramLongPollingBot {

    private Predictor predictor = new Predictor();
    private Logger log = Logger.getAnonymousLogger();

    /**
     * Метод для подключения к базе данных на Heroku.
     * JDBC_DATABASE_URL - содержит url(username, password, host etc.) для подключения
     */
    private static Connection getConnection() throws URISyntaxException, SQLException {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Метод для приема сообщений.
     * @param update Содержит сообщение от пользователя.
     */
    @Override
    public void onUpdateReceived(Update update) {
        String message = update.getMessage().getText();
        String output = getContactName(update) + getContactPhone(update);
        String answer;

        if(message == null && !output.equals(""))
            answer = predictor.ask("/start");
        else
            answer = predictor.ask(message);

        System.out.println(output + "[QUESTION]: " + message);
        System.out.println(output + "[ANSWER]: " + answer);
        sendMsg(update.getMessage().getChatId().toString(), answer);

    }

    /**
     * Метод для настройки сообщения и его отправки.
     * @param chatId id чата
     * @param s Строка, которую необходимо отправить в качестве сообщения.
     */
    public synchronized void sendMsg(String chatId, String s) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.enableMarkdown(true);
        sendMessage.setChatId(chatId);
        sendMessage.setText(s);
        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            log.log(Level.SEVERE, "Exception: ", e.toString());
        }
    }

    /**
     * Метод возвращает имя бота, указанное при регистрации.
     * @return имя бота
     */
    @Override
    public String getBotUsername() {
        return "PrimussBot";
    }

    /**
     * Метод возвращает token бота для связи с сервером Telegram
     * @return token для бота
     */
    @Override
    public String getBotToken() {
        return "895424372:AAHlLz11OTa2ZuQaTIG9mxffgu6R32XY2Xw";
    }

    public static void main(String[] args)
    {
        ApiContextInitializer.init();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();
        try {
            telegramBotsApi.registerBot(Bot.getBot());
        } catch (TelegramApiRequestException e) {
            e.printStackTrace();
        }
    }

    private static LongPollingBot getBot() {
        return new Bot();
    }

    private String getContactName(Update update) {

        String name;

        try {
            name = "[" + update.getMessage().getContact().getFirstName() + "]";
        } catch (Exception e) {
            return "";
        }

        return name;
    }
    private String getContactPhone(Update update)
    {
        String phone;

        try {
            phone = "[" + update.getMessage().getContact().getFirstName() + "]";
        } catch (Exception e) {
            return "";
        }

        return phone;
    }
}
