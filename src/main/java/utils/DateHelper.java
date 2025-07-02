package utils;

import java.time.DateTimeException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
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

    /**
     * Parse a human-friendly date descriptor to a {@link LocalDate}.
     * Supported descriptors: today, tomorrow, next week, next weekend,
     * next month and "next <day-of-week>" (e.g. next monday).
     */
    public static LocalDate parseFriendly(String descriptor) {
        if (descriptor == null || descriptor.isBlank()) {
            throw new IllegalArgumentException("Descriptor must not be empty");
        }
        String desc = descriptor.toLowerCase().trim();
        LocalDate now = LocalDate.now();
        return switch (desc) {
            case "today" -> now;
            case "tomorrow" -> now.plusDays(1);
            case "next week" -> now.with(TemporalAdjusters.next(DayOfWeek.MONDAY));
            case "next weekend" -> now.with(TemporalAdjusters.next(DayOfWeek.SATURDAY));
            case "next month" -> now.plusMonths(1).withDayOfMonth(1);
            default -> {
                if (desc.startsWith("next ")) {
                    DayOfWeek dow = parseDayOfWeek(desc.substring(5));
                    if (dow != null) {
                        yield now.with(TemporalAdjusters.next(dow));
                    }
                }
                throw new IllegalArgumentException("Unsupported descriptor: " + descriptor);
            }
        };
    }

    private static DayOfWeek parseDayOfWeek(String day) {
        return switch (day.toLowerCase()) {
            case "monday", "mon" -> DayOfWeek.MONDAY;
            case "tuesday", "tue" -> DayOfWeek.TUESDAY;
            case "wednesday", "wed" -> DayOfWeek.WEDNESDAY;
            case "thursday", "thu" -> DayOfWeek.THURSDAY;
            case "friday", "fri" -> DayOfWeek.FRIDAY;
            case "saturday", "sat" -> DayOfWeek.SATURDAY;
            case "sunday", "sun" -> DayOfWeek.SUNDAY;
            default -> null;
        };
    }
}