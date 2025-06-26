package utils;

import java.text.DecimalFormat;

public class NumberHelper {

    public static int parsePrice(String priceText) {
        // e.g. "1,234" → "1234" , "1,234 VND" → "1234"
        String digits = priceText.replaceAll("[^0-9]", "");
        return Integer.parseInt(digits);
    }

    public static String formatPrice(int price) {
        // e.g. "1234" → "1,234"
        DecimalFormat formatter = new DecimalFormat("#,###");
        return formatter.format(price);
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
