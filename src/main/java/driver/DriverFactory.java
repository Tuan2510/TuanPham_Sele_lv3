package driver;

import com.codeborne.selenide.Configuration;
import utils.LanguageManager;
import utils.RunConfigReader;
import static com.codeborne.selenide.Selenide.open;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class DriverFactory {

    public static void initDriver() {
        String browser = RunConfigReader.getOrDefault("browser", "chrome");
        Configuration.browser = browser;

        String hostUrl = RunConfigReader.get("hostUrl");
        if (hostUrl != null && !hostUrl.isBlank()) {
            Configuration.remote = hostUrl;
        } else {
            Configuration.remote = null; // run locally
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
