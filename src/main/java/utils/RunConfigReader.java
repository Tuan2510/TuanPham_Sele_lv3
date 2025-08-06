package utils;

import commons.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RunConfigReader {

    private static final Properties props = new Properties();
    private static final ThreadLocal<Properties> threadProps = ThreadLocal.withInitial(Properties::new);

    public static void loadConfiguration() {
        props.clear();
        try (InputStream input = RunConfigReader.class.getClassLoader()
                .getResourceAsStream(Constants.CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // apply overrides from system and TestNG xml parameters if available
        props.putAll(System.getProperties());
    }

    public static void setThreadProperties(Properties p) {
        if (p == null) {
            threadProps.remove();
        } else {
            Properties copy = new Properties();
            copy.putAll(props);
            copy.putAll(p);
            threadProps.set(copy);
        }
    }

    public static String get(String key) {
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
            case "books" -> get("books.baseUrl");
            default -> get("dev.baseUrl");
        };
    }
}
