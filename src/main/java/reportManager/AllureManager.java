package reportManager;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

import java.io.IOException;

public class AllureManager {

    public static void setupAllureReporting() {
        SelenideLogger.addListener("Allure",
                new AllureSelenide()
                        .screenshots(true)
                        .savePageSource(false)
                        .includeSelenideSteps(true)
        );
    }

    public static void generateAllureReport() throws IOException, InterruptedException {
        String resultsDir = "target/allure-results";
        String reportDir = "target/allure-report";

        ProcessBuilder pb = new ProcessBuilder(
                "allure", "generate", resultsDir, "-o", reportDir, "--clean"
        );
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Allure report generation failed with exit code: " + exitCode);
        }
    }

}
