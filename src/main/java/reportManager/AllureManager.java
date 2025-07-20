package reportManager;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    public static void copyAllureResult() throws IOException, InterruptedException {
        Path resultsDir = Paths.get("allure-results");
        Path reportDir = Paths.get("allure-report");

        Path historySrc = reportDir.resolve("history");
        Path historyDest = resultsDir.resolve("history");
        if (Files.exists(historySrc)) {
            if (Files.exists(historyDest)) {
                FileUtils.deleteDirectory(historyDest.toFile());
            }
            Files.createDirectories(historyDest);
            FileUtils.copyDirectory(historySrc.toFile(), historyDest.toFile());
        }
    }

    public void generateAllureReport() throws IOException, InterruptedException {
        Path resultsDir = Paths.get("allure-results");
        Path reportDir = Paths.get("allure-report");

        ProcessBuilder pb = new ProcessBuilder("allure", "serve", "allure-results");
        pb.inheritIO();
        Process process = pb.start();
        process.waitFor();
    }
}
