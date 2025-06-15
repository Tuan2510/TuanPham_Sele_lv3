package commons;

import utils.RunConfigReader;

public class Constants {
    public static final String RESOURCE_TEST_DATA_PATH = "src/test/resources/testdata/";
    public static final long ELEMENT_WAIT_MS = 20000;
    public static final int MAX_RETRY = Math.min(Integer.parseInt(RunConfigReader.get("retry.count")), 2);
    public static final String RETRY_STRATEGY = RunConfigReader.get("retry.mode");
    public static final String CONFIG_FILE = "config/RunConfiguration.properties";
}
