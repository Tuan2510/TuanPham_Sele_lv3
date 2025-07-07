package utils;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

public class DateHelper {
    /**
     * Get the formatted month
     */
    public static String getFormattedMonth(LocalDate date, Locale locale) {
        if ("vi".equalsIgnoreCase(locale.getLanguage())) {
            return String.format(locale, "tháng %02d", date.getMonthValue());
        }
        return date.getMonth().getDisplayName(TextStyle.FULL, locale);
    }

    public static String getFormattedYearMonth(YearMonth yearMonth, Locale locale, String format) {
        if (format == null || format.isBlank()) {
            format = "MM/yyyy"; // default fallback
        }
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format, locale);
            return yearMonth.format(formatter);
        } catch (IllegalArgumentException | DateTimeException e) {
            throw new RuntimeException("Invalid date format: " + format, e);
        }
    }
}
