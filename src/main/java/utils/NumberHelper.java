package utils;

import java.text.DecimalFormat;

public class NumberHelper {

    /**
     * Parse a price to number
     */
    public static int parsePrice(String priceText) {
        // e.g. "1,234" → "1234" , "1,234 VND" → "1234"
        String digits = priceText.replaceAll("[^0-9]", "");
        return Integer.parseInt(digits);
    }

    /**
     * Format number with decimal separator
     */
    public static String formatPrice(int price) {
        // e.g. "1234" → "1,234"
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price);
    }

    /**
     * Get the suffix for the day of month
     */
    public static String getNumberSuffix(int n) {
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
