package pageObjects.VJPageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import utils.LanguageManager;

import java.time.LocalDate;
import java.time.YearMonth;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static utils.ElementHelper.scrollToElement;
import static utils.NumberHelper.parsePrice;

public class VJSelectFlightCheapPage {
    // Locators

    //String Locators

    // Dynamic Locators
    private final String monthListXpath = "//div[div/p[contains(text(), '%s')]]//div[@class='slick-track']//div[contains(@class, 'slick-slide')]";
    private final String monthPriceXpath = "//div[div/p[contains(text(), '%s')]]//div[p[contains(text(), '%s')]]//span[1]";
    private final String prevMonthButtonXpath = "//div[div/p[contains(text(), '%s')]]//div[@class='slick-slider slick-initialized']//button[1]";
    private final String nextMonthButtonXpath = "//div[div/p[contains(text(), '%s')]]//div[@class='slick-slider slick-initialized']//button[2]";

    //Additional Locators
    private final String monthDisplayValueAdditionalXpath = "/div[1]";
    private final String monthPriceAdditionalXpath = "/div//p/span[1]";

    // Methods
    private SelenideElement getMonthPriceCollection(String dynamicValue) {

        return null;
    }

    private void navigateToDepartMonth(LocalDate date) {

    }


    private void findLowestPriceMonth(LocalDate startDate, LocalDate endDate) {

    }




}
