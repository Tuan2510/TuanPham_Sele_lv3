package utils;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;
import java.util.Properties;

public class LanguageManager {
    private static final Properties langProps = new Properties();
    @Getter
    private static final String language;

    static {
        language = RunConfigReader.getOrDefault("language", "en-us");
        String languageFolder = extractTestCasePackage();
        String resourcePath = "language/" + languageFolder + "/" + language + ".properties";

        try (InputStream input = LanguageManager.class.getClassLoader()
                .getResourceAsStream(resourcePath)) {
            if (input != null) {
                langProps.load(input);
            } else {
                throw new RuntimeException("Language file not found: " + resourcePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load language properties", e);
        }
    }

    private static String extractTestCasePackage() {
        for (StackTraceElement ste : Thread.currentThread().getStackTrace()) {
            String className = ste.getClassName();
            if (className.startsWith("testcases.")) {
                String[] parts = className.split("\\.");
                if (parts.length >= 2) {
                    return parts[1]; // e.g., VJTest
                }
            }
        }
        return ""; // fallback to root language/ directory
    }

    public static String get(String key) {
        return langProps.getProperty(key, key);
    }

    public static Locale getLocale(){
        return switch (LanguageManager.getLanguage().toLowerCase()) {
            case "vi-vn" -> Locale.of("vi", "VN");
            case "en-us" -> Locale.ENGLISH;
            default -> Locale.ENGLISH;
        };
    }

    public static String getLanguagePath() {
        return switch (language) {
            case "vi-vn" -> "/vi";
            default -> "/en";
        };
    }
}
