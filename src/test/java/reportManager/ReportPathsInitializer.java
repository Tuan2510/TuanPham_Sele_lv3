package reportManager;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportPathsInitializer {

    public static final String TIMESTAMP =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    public static final String EXTENT_DIR =
            "target/reports/extent-report/" + TIMESTAMP ;

    public static final String ALLURE_RESULTS_DIR =
            "target/reports/" + TIMESTAMP + "/allure-results";

    public static void createReportFolders(){
        File dir = new File(EXTENT_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }

        dir = new File(ALLURE_RESULTS_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }
}
