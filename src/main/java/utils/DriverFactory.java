package utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.RunConfigReader;

import java.net.URL;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class DriverFactory {
    protected static final Logger logger = LoggerFactory.getLogger(DriverFactory.class);

    static {
        RunConfigReader.loadConfiguration();
    }

    public static void initDriver() {
        logger.info("initDriver - Start");
        Configuration.timeout = Long.parseLong(RunConfigReader.get("timeout"));
        Configuration.browser = RunConfigReader.get("browser").toLowerCase();
        logger.info("initDriver - End");
    }

    public static void quitDriver() {
        logger.info("quitDriver - Start");
        closeWebDriver();
        logger.info("quitDriver - End");
    }
}
