package reportManager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ReportPathsInitializer {

    public static final String TIMESTAMP =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    public static final String EXTENT_DIR =
            "target/reports/" + TIMESTAMP + "/extent-report";

    public static final String ALLURE_RESULTS_DIR =
            "target/reports/" + TIMESTAMP + "/allure-results";
}
