import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Predictor {
    private Random random = new Random();
    private String[] answers = {
            "Да",
            "Нет",
            "Возможно",
            "Скорее всего нет",
            "Забудь",
            "Ты дибил?",
            "Верь в себя...",
            "Скорее всего да",
            "Спроси попозже",
            "Конечно!",
            "Ты же уже спрашивал..."

    };

    private String[] badQuestions = {
            "(К|к)ак",
            "(П|п)очему",
            "(Г|г)де",
            "(Ч|ч)то",
            "(К|к)то"

    };

    private String[] greetings = {
            "Привет",
            "Приветствую",
            "Здраствуйте",
            "Здрасте",
            "Здравствуйте",
            "Доброго времени суток",
            "Рад приветствовать"
    };

    public Predictor()
    {

    }

    public String ask(String question)
    {
        if(question.equals("/start"))
        {
            return "Привет! Я бот-предсказатель.\n" +
                    "Хочешь знать будущее? Можешь задать вопрос и я отвечу...\n" +
                    "Только не спрашивай как и почему и т.п., я отвечаю только утвердительно и предсказываю события которые тебя волнуют\n";
        }

        String badQuestion1 = "";
        String badQuestion2 = "";
        for(String regex : badQuestions) {
            badQuestion1 = Regexer.parse(question, "^" + regex + ".*$");
            badQuestion2 = Regexer.parse(question, "^.*" + regex + "$");

            if(!badQuestion1.equals("") || !badQuestion2.equals(""))
                return "На такие вопросы я не отвечаю";
        }

        for(String greeting : greetings)
        {
            if(!Regexer.parse(question, "^" + greeting + ".*$").equals(""))
                return greetings[random.nextInt(greetings.length)] + "!";
        }

        return answers[random.nextInt(answers.length)];
    }

    static class Regexer
    {
        static String parse(String msgData, String regex)
        {
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(msgData);
            String msgParsing = "";
            while(matcher.find())
            {
                msgParsing += msgData.substring(matcher.start(), matcher.end());
            }

            return msgParsing;
        }
    }

    public static void main(String[] args) throws IOException {
        Predictor predictor = new Predictor();
        BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        while (true)
        {
            String question = reader.readLine();
            if(!question.equals(""))
                System.out.println(predictor.ask(question));
        }

    }
}
