package utils;

import com.codeborne.selenide.Configuration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import utils.RunConfigReader;

public class DriverFactory {

    static {
        RunConfigReader.loadConfiguration();
    }

    public static void initDriver() {
        Configuration.browser = "chrome"; // Can be parameterized
        Configuration.timeout = Long.parseLong(RunConfigReader.get("timeout"));
//        Configuration.timeout = 10000;
    }

}
