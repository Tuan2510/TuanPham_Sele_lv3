package utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public class DateHelper {
    /**
     * Get the formatted month
     */
    public static String getFormattedMonth(LocalDate date, Locale locale, String format) {
        if (format == null || format.isBlank()) {
            format = "MMM"; // default fallback
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, locale);
            return date.format(formatter);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new RuntimeException("Invalid date format: " + format, e);
        }
    }

    public static String getFormattedDate(LocalDate date, Locale locale, String format) {
        if (format == null || format.isBlank()) {
            format = "MM/yyyy"; // default fallback
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, locale);
            return date.format(formatter);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new RuntimeException("Invalid date format: " + format, e);
        }
    }
}
