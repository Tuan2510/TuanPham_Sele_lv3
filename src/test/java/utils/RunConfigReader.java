package utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class RunConfigReader {

    private static final String CONFIG_FILE = "config/RunConfiguration.properties";
    private static final Properties props = new Properties();

    public static void loadConfiguration() {
        try (InputStream input = RunConfigReader.class.getClassLoader().getResourceAsStream(CONFIG_FILE)) {
            if (input != null) {
                props.load(input);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static String get(String key) {
        return props.getProperty(key, "");
    }
}
