package utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.text.DecimalFormat;
import java.time.Duration;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValueHelper {

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

    public static String getSafeText(SelenideElement parent, String cssSelector) {
        try {
            return parent.$(cssSelector).shouldBe(Condition.visible, Duration.ofSeconds(5)).getText();
        } catch (Exception e) {
            return "";
        }
    }

    public static float parseHotelRatingFloat(String rawText) {
        if (rawText == null || rawText.isEmpty()) {
            return 0f;
        }

        // Extract first number from the string (e.g., "5 stars out of 5" => 5)
        Pattern pattern = Pattern.compile("(\\d+(\\.\\d+)?)");
        Matcher matcher = pattern.matcher(rawText.trim());

        if (matcher.find()) {
            try {
                return Float.parseFloat(matcher.group(1));
            } catch (NumberFormatException e) {
                // Fallback in case parsing fails
                throw new RuntimeException("Failed to parse hotel rating from: " + rawText);
            }
        }

        return 0f; // Default if no match
    }

    /**
     * Parse a duration string to minutes
     * Example input: "0h 55m", "1h 20m", "2h 5m"
     */
    public static int parseDurationToMinutes(String text) {
        int hours = 0, mins = 0;

        if (text.contains("h")) {
            hours = Integer.parseInt(text.split("h")[0].trim());
            text = text.split("h")[1];
        }
        if (text.contains("m")) {
            mins = Integer.parseInt(text.replace("m", "").trim());
        }

        return hours * 60 + mins;
    }

}
