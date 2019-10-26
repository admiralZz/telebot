import java.util.Random;

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

    public Predictor()
    {

    }

    public String ask(String question)
    {
        return answers[random.nextInt(answers.length)];
    }
}
