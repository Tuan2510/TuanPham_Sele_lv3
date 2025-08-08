package commons;

import utils.RunConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String RESOURCE_TEST_DATA_PATH = "src/test/resources/testdata/";
    public static final String CONFIG_FILE = "RunConfiguration.properties";

    public static final int MAX_RETRY = Integer.parseInt(RunConfigReader.getOrDefault("retry.count", "0"));
    public static final String RETRY_STRATEGY = RunConfigReader.getOrDefault("retry.mode", "afterDone");
    public static final String TIMESTAMP = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    public static final String REPORT_DIR = "extent-reports/" + TIMESTAMP;

    public static final String LEAP_FROG_EXCEL_PATH = "src/test/resources/testdata/LeapFrog/leapfrog-games.xlsx";
    public static final String LEAP_FROG_TEST_URL = "https://store.leapfrog.com/en-us/apps/c?p=%s&platforms=197&product_list_dir=asc&product_list_order=name";

    public static final int DEFAULT_SCROLL_STEPS = 10;
}
