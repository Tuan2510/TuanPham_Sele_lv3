package utils;

public class NumberHelper {

    private int parsePrice(String priceText) {
        // e.g. "1.234.567 VND" → "1234567"
        String digits = priceText.replaceAll("[^0-9]", "");
        return Integer.parseInt(digits);
    }

}
