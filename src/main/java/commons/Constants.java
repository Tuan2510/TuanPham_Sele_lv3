package commons;

import utils.RunConfigReader;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Constants {
    public static final String RESOURCE_TEST_DATA_PATH = "src/test/resources/testdata/";
    public static final String CONFIG_FILE = "config/RunConfiguration.properties";

    static {
        RunConfigReader.loadConfiguration();
    }

    public static final int MAX_RETRY = Integer.parseInt(RunConfigReader.getOrDefault("retry.count", "1"));
    public static final String RETRY_STRATEGY = RunConfigReader.getOrDefault("retry.mode", "afterDone");
    public static final String TIMESTAMP = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

    public static final String REPORT_DIR = "extent-reports/" + TIMESTAMP;
    public static final String RETURN_FLIGHT = "Return";
    public static final String ONEWAY_FLIGHT = "One-way flight";
    public static final String CURRENCY = "VND";
}
