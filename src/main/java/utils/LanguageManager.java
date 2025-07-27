package utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.Properties;

public class LanguageManager {
    private static final ThreadLocal<String> currentLanguage = ThreadLocal.withInitial(() ->
            RunConfigReader.getOrDefault("language", "en-us"));

    private static final ThreadLocal<Properties> languageProps = new ThreadLocal<>();

    public static void setLanguage(String lang) {
        if (lang != null && !lang.isBlank()) {
            currentLanguage.set(lang);
            languageProps.remove();
        }
    }

    /**
     * Clear any loaded language data for the current thread.
     * This should be called after each test method to ensure the next method
     * reloads language properties fresh.
     */
    public static void clearCache() {
        languageProps.remove();
    }

    private static Properties loadLanguageProps() {
        Properties p = languageProps.get();
        if (p != null && p.get("language").equals(currentLanguage.get())) {
            return p;
        }

        String languageFolder = extractTestCasePackage();
        String resourcePath = "language/" + languageFolder + "/" + currentLanguage.get() + ".properties";

        p = new Properties();
        try (InputStream input = LanguageManager.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (input != null) {
                p.load(new InputStreamReader(input, StandardCharsets.UTF_8));
            } else {
                throw new RuntimeException("Language file not found: " + resourcePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Could not load language properties", e);
        }
        languageProps.set(p);
        return p;
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
        return loadLanguageProps().getProperty(key);
    }

    public static Locale getLocale(){
        return switch (getLanguage().toLowerCase()) {
            case "vi-vn" -> Locale.of("vi", "VN");
            case "en-us" -> Locale.ENGLISH;
            default -> Locale.ENGLISH;
        };
    }

    public static String getLanguagePath() {
        String env = RunConfigReader.getOrDefault("env", "dev").toLowerCase();
        String language = getLanguage();

        return switch (env) {
            case "dev" -> switch (language) {
                case "vi-vn" -> "/vi-vn";
                default -> "/en-us";
            };
            case "stg", "stage" -> switch (language) {
                case "vi-vn", "vi" -> "/vi";
                default -> "/en";
            };
            default -> "/en"; // fallback
        };
    }



    public static String getLanguage() {
        return currentLanguage.get();
    }
}
