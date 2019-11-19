import java.util.Random;

public class Admin {

    private String[] answers = {
            "Я вас не понимаю" ,
            "Вы себя хорошо чувствуете?",
            "Вы забыли правила общения",
            "Я тоже хотел бы знать...",
            "Ключевые слова..."};

    private final static String ON_COMMAND = "/admin";
    private final static String PASSWORD = "Сим сим откройся!";
    private boolean isActivate;
    private boolean isAccessOpen;

    private Random random = new Random();
    private DataBase dataBase;

    /**
     * Конструктор
     * @param dataBase - база данных
     */
    public Admin(DataBase dataBase)
    {
        this.dataBase = dataBase;
    }

    /**
     * Метод приёма сообщений
     * @param message - входящее сообщение
     * @return - исходящие сообщение
     */
    public String onUpdateReceived(String message)
    {
        if(message.equals(ON_COMMAND))
        {
            if(isAccessOpen)
                return "[ADMIN]: Я уже к вашим услугам, не стоит меня звать...";

            isActivate = true;
            return "[ADMIN]: Введите секретное слово...";
        }
        else if(!isAccessOpen)
        {
            if(message.equals(PASSWORD)) {
                isAccessOpen = true;
                return "[ADMIN]: Рад служить вам Господин";
            }

            isActivate = false;
            return "[ADMIN]: Ты не посвященный...";
        }

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
                isAccessOpen = false;
                isActivate = false;
            }
            break;

            default:
                answer = answers[random.nextInt(answers.length)];
                break;
        }

        return answer;
    }

    public boolean isActivate()
    {
        return isActivate;
    }

}
