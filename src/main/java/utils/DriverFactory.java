package utils;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import utils.RunConfigReader;

import java.net.URL;

public class DriverFactory {
    private static final ThreadLocal<WebDriver> tlDriver = new ThreadLocal<>();

    static {
        RunConfigReader.loadConfiguration();
    }

    public static void initDriver() {
        Configuration.timeout = Long.parseLong(RunConfigReader.get("timeout"));

        if (tlDriver.get() == null) {
            String browser = RunConfigReader.get("browser").toLowerCase();
            WebDriver driver;
            try {
                switch (browser) {
                    case "chrome":
                        driver = new ChromeDriver();
                        break;
                    case "edge":
                        driver = new EdgeDriver();
                        break;
                    case "firefox":
                        driver = new FirefoxDriver();
                        break;
                    case "grid-chrome":
                        URL gridUrl = new URL(RunConfigReader.get("grid.url"));
                        MutableCapabilities caps = (MutableCapabilities) new ChromeDriver().getCapabilities();
                        driver = new RemoteWebDriver(gridUrl, caps);
                        break;
                    default:
                        throw new IllegalArgumentException("Unsupported browser: " + browser);
                }
            } catch (Exception e) {
                throw new RuntimeException("Error creating WebDriver", e);
            }
            WebDriverRunner.setWebDriver(driver);
            tlDriver.set(driver);
        }
    }

    public static WebDriver getDriver() {
        return tlDriver.get();
    }

    public static void quitDriver() {
        WebDriver driver = tlDriver.get();
        if (driver != null) {
            driver.quit();
            tlDriver.remove();
        }
    }
}
