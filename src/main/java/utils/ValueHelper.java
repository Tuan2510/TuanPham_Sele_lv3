package utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.text.DecimalFormat;
import java.time.Duration;

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

    public static float parseFloatSafe(String value) {
        try {
            return Float.parseFloat(value);
        } catch (NumberFormatException e) {
            return 0f;
        }
    }

}
