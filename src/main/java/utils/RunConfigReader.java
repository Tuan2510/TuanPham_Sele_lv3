package utils;

import commons.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RunConfigReader {

    private static final Properties props = new Properties();

    public static void loadConfiguration() {
        try (InputStream input = RunConfigReader.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys;
        }
        return props.getProperty(key);
    }

    public static String getOrDefault(String key, String defaultValue) {
        String value = get(key);
        return value != null ? value : defaultValue;
    }

    public static String getBaseUrl() {
        String env = get("env");
        if (env == null || env.isBlank()) {
            env = "dev";
        }

        switch (env.toLowerCase()) {
            case "test":
                return get("test.baseUrl");
            case "stg":
                return get("stg.baseUrl");
            case "dev":
            default:
                return get("dev.baseUrl");
        }
    }
}
