package driver;

import com.codeborne.selenide.Configuration;
import com.codeborne.selenide.WebDriverRunner;
import utils.LanguageManager;
import utils.RunConfigReader;
import static com.codeborne.selenide.Selenide.open;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class DriverFactory {

    public static void initDriver() {
        if (WebDriverRunner.hasWebDriverStarted()) {
            return;
        }

        String gridUrl = RunConfigReader.get("gridUrl");
        if (gridUrl != null && !gridUrl.isBlank()) {
            Configuration.remote = gridUrl;
            Configuration.browser = RunConfigReader.getOrDefault("browser", "chrome");
        } else {
            Configuration.remote = null;
        }
    }

    public static void quitDriver() {
        closeWebDriver();
    }

    public static void openHomePage() {
        initDriver();
        String url = RunConfigReader.getBaseUrl();

        if (url == null || url.isBlank()) {
            url = "https://www.youtube.com";
        }

        if (url.endsWith("/")) {
            url = url.substring(0, url.length() - 1);
        }

        if(RunConfigReader.get("env").equalsIgnoreCase("stg")){
            url = url + LanguageManager.getLanguagePath();
        }

        open(url);
    }

}
