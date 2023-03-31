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

        for(long i = 1; i <= 30; i++) {
            long chatId = i;
            executorService.submit(() -> {
                User user = new User(manager, chatId);
                threads.put(chatId, user);
                user.startChat();
            });
            Thread.sleep(500);
        }

        executorService.awaitTermination(60, TimeUnit.SECONDS);
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
            startTime = System.currentTimeMillis();
            manager.chat(chatId, "user" + chatId, "Чем занимаешься?");
        }

        void sendAnswer(String answer) {
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
