package reportManager;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.commons.io.FileUtils;

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
        String resultsDir = "allure-results";
        String reportDir = "allure-report";

        Path historySrc = Path.of(reportDir, "history");
        Path historyDest = Path.of(resultsDir, "history");
        if (Files.exists(historySrc)) {
            if (Files.exists(historyDest)) {
                FileUtils.deleteDirectory(historyDest.toFile());
            }
            Files.createDirectories(historyDest);
            FileUtils.copyDirectory(historySrc.toFile(), historyDest.toFile());
        }

        ProcessBuilder pb = new ProcessBuilder("allure", "generate", resultsDir, "-o", reportDir, "--clean");
        pb.inheritIO();
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Allure report generation failed with exit code: " + exitCode);
        }

        new ProcessBuilder("allure", "open", reportDir).inheritIO().start();
    }

}
