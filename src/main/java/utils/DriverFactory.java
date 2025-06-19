package utils;

import com.codeborne.selenide.Configuration;
import commons.Constants;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeOptions;
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

        Configuration.remote = RunConfigReader.get("remoteURL");
        Configuration.browser = System.getProperty("browser", "chrome");;


        logger.info("initDriver - End");
    }

    public static void quitDriver() {
        logger.info("quitDriver - Start");
        closeWebDriver();
        logger.info("quitDriver - End");
    }
}
