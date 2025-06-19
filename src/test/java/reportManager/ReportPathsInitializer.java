package reportManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportPathsInitializer {

    public static final String TIMESTAMP =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    public static final String REPORT_DIR = "target/reports/" + TIMESTAMP;

    public static final String EXTENT_REPORT_FILE = REPORT_DIR + "/extent-report.html";
    public static final String ALLURE_RESULTS_DIR = REPORT_DIR + "/allure-result";

    public static void createReportFolders(){
        File extentDir = new File(REPORT_DIR);
        if (!extentDir.exists()) {
            extentDir.mkdirs();
        }

        File allureDir = new File(ALLURE_RESULTS_DIR);
        if (!allureDir.exists()) {
            allureDir.mkdirs();
        }
    }
}
