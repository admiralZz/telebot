import java.net.URISyntaxException;
import java.sql.*;
import java.util.ArrayList;

public class DataBase {

    public DataBase()
    {

    }

    /**
     * Метод для подключения к базе данных на Heroku.
     * JDBC_DATABASE_URL - содержит url(username, password, host etc.) для подключения
     */
    private static Connection getConnection() throws URISyntaxException, SQLException {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            System.err.println("Postgresql driver is not found");
            e.printStackTrace();
        }

        String dbUrl = System.getenv("JDBC_DATABASE_URL");

        return DriverManager.getConnection(dbUrl);
    }

    /**
     * Метод добавления строки в БД таблицу messages
     * @param chatId - id чата(пользователя) в котором общается бот
     * @param question - вопрос который задан боту пользователем
     * @param answer - ответ который дал бот пользователю
     */
    public void insert(String chatId, String question, String answer)
    {
        try(Connection connection = getConnection();
            Statement statement = connection.createStatement())
        {
            statement.execute("insert into messages(chat_id,question,answer) values('" + chatId + "','" + question + "','" + answer + "');");

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    /**
     * Метод чтения базы данных, а именно таблицы сообщений
     * @return - список строк из таблицы messages
     */
    public ArrayList<String> read(){

        ArrayList<String> rows = new ArrayList<>();

        try(Connection connection = getConnection();
            Statement statement = connection.createStatement())
        {
            ResultSet rs = statement.executeQuery("select * from messages");
            while (rs.next())
            {
                String row = "";

                row += "[CHAT_ID]:" + rs.getString("chat_id") + " ";
                row += "[QUESTION]:" + rs.getString("question") + " ";
                row += "[ANSWER]:" + rs.getString("answer") + " ";

                rows.add(row);
            }

            return rows;

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        return null;
    }

}
