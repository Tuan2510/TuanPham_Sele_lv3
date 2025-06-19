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
        //String runMode  = RunConfigReader.get("runMode").toLowerCase(); // local or grid

        String runMode = System.getProperty("runMode", "local"); // local or grid
        String browser = System.getProperty("browser", "chrome");

        if ("grid".equalsIgnoreCase(runMode)) {
            // Setup for Selenium Grid
            try {
                MutableCapabilities capabilities;
                switch (browser.toLowerCase()) {
                    case "edge":
                        capabilities = new EdgeOptions();
                        break;
                    case "chrome-headless":
                        capabilities = new ChromeOptions().addArguments("--headless");
                        break;
                    case "chrome":
                    default:
                        capabilities = new ChromeOptions();
                        break;
                }

                Configuration.remote = RunConfigReader.get("remoteURL");
                Configuration.browserCapabilities = capabilities;

            } catch (Exception e) {
                throw new RuntimeException("Failed to setup remote driver", e);
            }
        } else {
            // Local driver setup
            switch (browser.toLowerCase()) {
                case "edge":
//                    WebDriverManager.edgedriver().setup();
                    Configuration.browser = "edge";
                    break;
                case "chrome":
                default:
//                    WebDriverManager.chromedriver().setup();
                    Configuration.browser = "chrome";
                    break;
            }
        }

        logger.info("initDriver - End");
    }

    public static void quitDriver() {
        logger.info("quitDriver - Start");
        closeWebDriver();
        logger.info("quitDriver - End");
    }
}
