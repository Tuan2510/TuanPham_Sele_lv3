package commons;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String RESOURCE_TEST_DATA_PATH = "src/test/resources/testdata/";
    public static final String CONFIG_FILE = "config/RunConfiguration.properties";

    public static final long ELEMENT_WAIT_MS = Long.parseLong(System.getProperty("retryStrategy", "20000"));
    public static final int MAX_RETRY = Math.min(Integer.parseInt(System.getProperty("retry.count", "1")), 1);
    public static final String RETRY_STRATEGY = System.getProperty("retry.mode", "afterDone");
    public static final String TIMESTAMP = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    public static final String REPORT_DIR = "reports/" + TIMESTAMP;
}
