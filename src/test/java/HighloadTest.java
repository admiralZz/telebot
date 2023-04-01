import com.admiral.telebot.gpt.GPTSession;
import com.admiral.telebot.gpt.GPTSessionManager;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class HighloadTest {
    private static final Logger log = LoggerFactory.getLogger(HighloadTest.class);
    private static final ExecutorService executorService = Executors.newWorkStealingPool();
    private static final List<Long> timeOfAnswers = new ArrayList<>();

    @Test
    public void test() throws InterruptedException {
        Map<Long, User> threads = new HashMap<>();

        GPTSessionManager manager = new GPTSessionManager((chatId, message) -> {
            threads.get(chatId).sendAnswer(message);
        });

        for(long i = 1; i <= 10; i++) {
            User user = new User(manager, i);
            threads.put(i, user);
            user.startChat();
            Thread.sleep(500);
        }

        executorService.awaitTermination(120, TimeUnit.SECONDS);
        threads.forEach(
                (chatId, user) -> log.info("user{} average time to answer: {}", chatId, toPrettyTime(user.getAverageTime()))
        );
    }

    private static Long averageTime() {
        OptionalDouble optionalDouble = timeOfAnswers.stream()
                .mapToLong(l -> l)
                .average();
        if(optionalDouble.isPresent()) {
            return (long) optionalDouble.getAsDouble();
        }
        return 0L;
    }
    private static String toPrettyTime(long millis) {
        return new SimpleDateFormat("ss:SSS").format((new Date(millis))) + " mc";
    }

    static class User {
        private final GPTSessionManager manager;
        private final Long chatId;
        private long startTime;

        private final List<Long> timeOfAnswers = new ArrayList<>();

        public User(GPTSessionManager manager, Long chatId) {
            this.manager = manager;
            this.chatId = chatId;
        }

        void startChat() {
            try {
//                Thread.sleep(1000);
                manager.chat(chatId, "user" + chatId, "Привет");
                Thread.sleep(200);
                manager.chat(chatId, "user" + chatId, "Чем занимаешься?");
                Thread.sleep(300);
                manager.chat(chatId, "user" + chatId, "Как жизнь?");
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            startTime = System.currentTimeMillis();
        }

        void sendAnswer(String answer) {
            if(Arrays.asList(GPTSession.preparedAnswers).contains(answer)) {
                log.debug("Received prepared answer \"{}\" for user{}", answer, chatId);
                return;
            }

            if(Arrays.asList(GPTSessionManager.preparedAnswers).contains(answer)) {
                log.debug("Received prepared answer \"{}\" for user{}", answer, chatId);
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                startChat();
                return;
            }
            long endTime = System.currentTimeMillis();
            long timeToAnswer = endTime - startTime;
            timeOfAnswers.add(timeToAnswer);
            log.info("Result for user{} time: {}\n payload: {}", chatId, toPrettyTime(timeToAnswer), answer);

            startChat();
        }

        private Long getAverageTime() {
            OptionalDouble optionalDouble = timeOfAnswers.stream()
                    .mapToLong(l -> l)
                    .average();
            if(optionalDouble.isPresent()) {
                return (long) optionalDouble.getAsDouble();
            }
            return 0L;
        }

        long getStartTime() {
            return startTime;
        }
    }
}
