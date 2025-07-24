package reportManager;

import java.io.File;

import static commons.Constants.REPORT_DIR;

public class ReportPathsInitializer {

    public static final String EXTENT_REPORT_FILE = REPORT_DIR + "/extent-report.html";
    public static final String SCREENSHOT_DIR = REPORT_DIR;

    public static void createReportFolders(){
        File extentDir = new File(REPORT_DIR);
        if (!extentDir.exists()) {
            extentDir.mkdirs();
        }

        File screenshotDir = new File(SCREENSHOT_DIR);
        if (!screenshotDir.exists()) {
            screenshotDir.mkdirs();
        }

        // configure Selenide to store screenshots inside this run's folder
        com.codeborne.selenide.Configuration.reportsFolder = SCREENSHOT_DIR;

    }
}
