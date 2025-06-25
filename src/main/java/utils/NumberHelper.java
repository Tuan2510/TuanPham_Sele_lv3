package utils;

public class NumberHelper {

    public static int parsePrice(String priceText) {
        // e.g. "1.234.567 VND" → "1234567"
        String digits = priceText.replaceAll("[^0-9]", "");
        return Integer.parseInt(digits);
    }

    public static String getDayOfMonthSuffix(int n) {
        if (n >= 11 && n <= 13) {
            return "th";
        }
        return switch (n % 10) {
            case 1 -> "st";
            case 2 -> "nd";
            case 3 -> "rd";
            default -> "th";
        };
    }

}
