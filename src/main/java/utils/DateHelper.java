package utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static utils.LanguageManager.getLocale;

public class DateHelper {

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

    public static YearMonth parseYearMonth(String text) {
        String sanitized = text.replaceAll("[^0-9/]", "").trim();
        return YearMonth.parse(sanitized, DateTimeFormatter.ofPattern("MM/yyyy", Locale.US));
    }

    public static String formatAsVietnameseShortDay(LocalDate date, String pattern) {
        String dayPrefix;
        switch (date.getDayOfWeek()) {
            case MONDAY -> dayPrefix = "T2";
            case TUESDAY -> dayPrefix = "T3";
            case WEDNESDAY -> dayPrefix = "T4";
            case THURSDAY -> dayPrefix = "T5";
            case FRIDAY -> dayPrefix = "T6";
            case SATURDAY -> dayPrefix = "T7";
            case SUNDAY -> dayPrefix = "CN";
            default -> throw new IllegalArgumentException("Invalid day");
        }

        // Fallback in case pattern is null or empty
        if (pattern == null || pattern.isBlank()) {
            pattern = "dd/MM/yyyy";
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern, new Locale("vi", "VN"));
        return "%s, %s".formatted(dayPrefix, date.format(formatter));
    }

    public static String formatShortDay(LocalDate date, String pattern) {
        switch (getLocale().getLanguage()) {
            case "vi":
                return formatAsVietnameseShortDay(date, pattern);
            case "en":
                return date.format(DateTimeFormatter.ofPattern(pattern, Locale.ENGLISH));
            default:
                throw new IllegalArgumentException("Unsupported locale: " + getLocale());
        }
    }

}
