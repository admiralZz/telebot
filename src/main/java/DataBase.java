import java.net.URISyntaxException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DataBase {

    public DataBase()
    {

    }

    /**
     * Метод для подключения к базе данных на Heroku.
     * JDBC_DATABASE_URL - содержит url(username, password, host etc.) для подключения
     */
    private static Connection getConnection() throws URISyntaxException, SQLException {
        String dbUrl = System.getenv("JDBC_DATABASE_URL");
        return DriverManager.getConnection(dbUrl);
    }


}
