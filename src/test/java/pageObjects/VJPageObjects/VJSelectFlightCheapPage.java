package pageObjects.VJPageObjects;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$;

public class VJSelectFlightCheapPage {
    // Locators
    private final SelenideElement acceptCookiesBtn = $("#popup-dialog-description + div button");

    // Dynamic Locators
    //div[div/p[contains(text(), 'Chuyến đi')]]//div[p[contains(text(), '07/2025')]]//span[1]
    //div[div/p[contains(text(), 'Chuyến đi')]]//div[p[contains(text(), 'Từ')]]
    private final String monthList = "//div[div/p[contains(text(), '%s')]]//div[p[contains(text(), '%s')]]";
    private final String monthLowestPrice = "//div[div/p[contains(text(), '%s')]]//div[p[contains(text(), '%s')]]//span[1]";
    private final String previousMonthButton = "//div[div/p[contains(text(), '%s')]]//button[1]";
    private final String nextMonthButton = "//div[div/p[contains(text(), '%s')]]//button[2]";

    // Methods





}
