package reportManager;

import java.io.IOException;
import java.nio.file.*;
import java.util.stream.Stream;

public class AllureReportHelper {

    public static void moveAllureResultsAndGenerateReport() throws IOException, InterruptedException {
        String sourceDir = "target/reports/allure-results";
        String targetBaseDir = ReportPathsInitializer.REPORT_DIR;

        String destResultsDir = ReportPathsInitializer.ALLURE_RESULTS_DIR;
        String destReportDir = targetBaseDir + "/allure-report";

        Path sourcePath = Paths.get(sourceDir);
        Path destResultsPath = Paths.get(destResultsDir);

        // Create destination dir
        if (!Files.exists(destResultsPath)) {
            Files.createDirectories(destResultsPath);
        }

        // Move files
        try (Stream<Path> paths = Files.list(sourcePath)) {
            paths.forEach(path -> {
                try {
                    Files.move(path, destResultsPath.resolve(path.getFileName()), StandardCopyOption.REPLACE_EXISTING);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to move file: " + path, e);
                }
            });
        }

        // Optionally delete source dir if empty
        Files.delete(sourcePath);

        // Generate Allure report
        ProcessBuilder pb = new ProcessBuilder(
                "allure", "generate", destResultsDir, "-o", destReportDir, "--clean"
        );
        pb.inheritIO();  // Show process output in console
        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("Allure report generation failed with exit code: " + exitCode);
        }

//        System.out.println("Allure HTML report generated at: " + destReportDir);
    }


}
