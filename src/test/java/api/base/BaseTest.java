package api.base;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import api.utils.TokenManager;

public class BaseTest {
    protected static final Logger logger = LoggerFactory.getLogger(BaseTest.class);
    protected String token;

    @BeforeEach
    void setUp() {
        logger.info("Setting up test...");
        token = TokenManager.getToken();
    }

    @AfterEach
    void tearDown() throws InterruptedException {
        logger.info("Test finished. Waiting for 1 second before next test.");
        //Added for rate limit
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}