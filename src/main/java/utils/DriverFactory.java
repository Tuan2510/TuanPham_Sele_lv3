package utils;

import com.codeborne.selenide.Configuration;
import commons.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class DriverFactory {
    protected static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);

    static {
        RunConfigReader.loadConfiguration();
    }

    public static void initDriver() {
        logger.info("initDriver - Start");
        Configuration.timeout = Constants.ELEMENT_WAIT_MS;
        Configuration.browser = RunConfigReader.get("browser").toLowerCase();
        logger.info("initDriver - End");
    }

    public static void quitDriver() {
        logger.info("quitDriver - Start");
        closeWebDriver();
        logger.info("quitDriver - End");
    }
}
