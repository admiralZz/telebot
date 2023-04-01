import com.admiral.telebot.conf.BotConfig;
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
    public void testChatWithControl() throws InterruptedException {
        Map<Long, User> threads = new HashMap<>();

        GPTSessionManager manager = new GPTSessionManager((chatId, message) -> {
            threads.get(chatId).putAnswer(message);
        });

        for(long i = 1; i <= 10; i++) {
            long chatId = i;
            User user = new User(i, () -> {
                try {
//                Thread.sleep(1000);
                    manager.chatWithControl(chatId, "user" + chatId, "Привет");
                    Thread.sleep(200);
                    manager.chatWithControl(chatId, "user" + chatId, "Чем занимаешься?");
                    Thread.sleep(300);
                    manager.chatWithControl(chatId, "user" + chatId, "Как жизнь?");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            });
            threads.put(i, user);
            user.startChat();
            Thread.sleep(500);
        }

        executorService.awaitTermination(120, TimeUnit.SECONDS);
        threads.forEach(
                (chatId, user) -> log.info("user{} average time to answer: {}", chatId, toPrettyTime(user.getAverageTime()))
        );
    }

    @Test
    public void testChat() throws InterruptedException {
        Map<Long, User> threads = new HashMap<>();

        GPTSessionManager manager = new GPTSessionManager((chatId, message) -> {
            threads.get(chatId).putAnswer(message);
        });

        for(long i = 1; i <= 10; i++) {
            long chatId = i;
            User user = new User(i, () -> {
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
            });
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
        private final Long chatId;
        private final Runnable runChat;
        private long startTime;
        private final List<Long> timeOfAnswers = new ArrayList<>();

        public User(Long chatId, Runnable runChat) {
            this.chatId = chatId;
            this.runChat = runChat;
        }

        void startChat() {
            runChat.run();
            startTime = System.currentTimeMillis();
        }

        void putAnswer(String answer) {
            if(Arrays.asList(BotConfig.preparedUserLimitAnswers).contains(answer)) {
                log.debug("Received prepared answer \"{}\" for user{}", answer, chatId);
                return;
            }

            if(Arrays.asList(BotConfig.preparedGlobalLimitAnswers).contains(answer)) {
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
