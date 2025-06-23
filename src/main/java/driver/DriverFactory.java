package driver;

import com.codeborne.selenide.Configuration;
import commons.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RunConfigReader;
import static com.codeborne.selenide.Selenide.open;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class DriverFactory {
    protected static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);

    static {
        RunConfigReader.loadConfiguration();
    }

    public static void initDriver() {
        logger.info("initDriver - Start");

        logger.info("initDriver - End");
    }

    public static void quitDriver() {
        logger.info("quitDriver - Start");
        closeWebDriver();
        logger.info("quitDriver - End");
    }

    public static void openHomePage() {
        String url = RunConfigReader.getBaseUrl();
        if (url == null || url.isBlank()) {
            url = "https://www.youtube.com";
        }
        open(url);
    }

}
