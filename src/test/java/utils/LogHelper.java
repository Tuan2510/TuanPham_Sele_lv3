package utils;

import io.qameta.allure.Allure;
import org.slf4j.Logger;

/**
 * Helper to log steps consistently to Logger, Extent, and Allure
 */
public class LogHelper {
    private final Logger logger;
    private final TestListener listenerRef;

    public LogHelper(Logger logger, TestListener listenerRef) {
        this.logger = logger;
        this.listenerRef = listenerRef;
    }

    /**
     * Logs a step to Logger, Extent, and Allure
     * @param message step description
     */
    public void logStep(String message) {
        if (logger != null) {
            logger.info(message);
        }

        if (listenerRef != null) {
            listenerRef.addStep(message);
        }

        io.qameta.allure.Allure.step(message);
    }
}