package utils;

import commons.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RunConfigReader {

    private static final Properties props = new Properties();
    private static final ThreadLocal<Properties> threadProps = ThreadLocal.withInitial(Properties::new);

    public static void loadConfiguration() {
        try (InputStream input = RunConfigReader.class.getClassLoader().getResourceAsStream(Constants.CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void setThreadProperties(Properties p) {
        if (p == null) {
            threadProps.remove();
        } else {
            Properties copy = new Properties();
            copy.putAll(p);
            threadProps.set(copy);
        }
    }

    public static String get(String key) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) {
            return sys;
        }

        String threadVal = threadProps.get().getProperty(key);
        if (threadVal != null && !threadVal.isBlank()) {
            return threadVal;
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

        return switch (env.toLowerCase()) {
            case "test" -> get("test.baseUrl");
            case "stg" -> get("stg.baseUrl");
            default -> get("dev.baseUrl");
        };
    }
}
