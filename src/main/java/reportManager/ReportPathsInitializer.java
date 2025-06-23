package reportManager;

import java.io.File;

import static commons.Constants.REPORT_DIR;

public class ReportPathsInitializer {

    public static final String EXTENT_REPORT_FILE = REPORT_DIR + "/extent-report.html";

    public static void createReportFolders(){
        File extentDir = new File(REPORT_DIR);
        if (!extentDir.exists()) {
            extentDir.mkdirs();
        }

    }
}
