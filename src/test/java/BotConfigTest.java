import com.admiral.telebot.conf.BotConfig;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BotConfigTest {
    private static final Logger log = LoggerFactory.getLogger(BotConfigTest.class);

    @Test
    public void testConfig() {
        BotConfig config = BotConfig.getInstance();
        Assert.assertNotNull(config.getGptApiUrl());
        Assert.assertNotNull(config.getGptModel());
        Assert.assertNotNull(config.getGptRole());
        Assert.assertNotNull(config.getGptTemperature());
        Assert.assertNotNull(config.getTelegramBotUsername());
        Assert.assertNotNull(config.getGptApiToken());
        Assert.assertNotNull(config.getTelegramBotApiToken());
    }
}
