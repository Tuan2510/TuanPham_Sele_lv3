package driver;

import com.codeborne.selenide.Configuration;
import commons.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.LanguageManager;
import utils.RunConfigReader;
import static com.codeborne.selenide.Selenide.open;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class DriverFactory {

    static {
        RunConfigReader.loadConfiguration();
    }

    public static void initDriver() {
    }

    public static void quitDriver() {
        closeWebDriver();
    }

    public static void openHomePage() {
        String url = RunConfigReader.getBaseUrl();

        if (url == null || url.isBlank()) {
            url = "https://www.youtube.com";
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }
        open(url + LanguageManager.getLanguagePath());
    }

}
