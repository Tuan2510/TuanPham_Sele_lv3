package pageObjects.VJPageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import utils.LanguageManager;
import utils.NumberHelper;

import java.time.LocalDate;
import java.time.YearMonth;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.ElementHelper.clickWhenReady;
import static utils.ElementHelper.scrollToElement;

public class VJSelectFlightCheapPage {
    // Locators
    private final SelenideElement continueButton = $("div.MuiBox-root > button.MuiButtonBase-root");

    // Dynamic Locators
    private final String prevMonthButtonXpath = "//div[div/p[contains(text(), '%s')]]//div[@class='slick-slider slick-initialized']//button[1]";
    private final String nextMonthButtonXpath = "//div[div/p[contains(text(), '%s')]]//div[@class='slick-slider slick-initialized']//button[2]";
    private final String monthListXpath = "//div[div/p[contains(text(), '%s')]]//div/div/div[contains(@class, 'slick-slide')]";
    private final String currentMonthXpath = "//div[div/p[contains(text(), '%s')]]//div[contains(@class, 'slick-current')]";
    private final String availableTicketListXpath = "//div[div/p[contains(text(), '%s')]]//div[@role='button'][.//div/span]";

    //Additional Locators
    private final String monthDisplayValueAdditionalXpath = ".//div/p[contains(@class, 'MuiTypography-h6')]";
    private final String monthPriceAdditionalXpath = "/div//p/span[1]";
    private final String ticketPriceAdditionalXpath = ".//span[not(contains(normalize-space(), 'VND'))]";

    // Methods
    private SelenideElement getPrevMonthButton(String dynamicValue) {
        return $x(prevMonthButtonXpath.formatted(dynamicValue));
    }

    private SelenideElement getNextMonthButton(String dynamicValue) {
        return $x(nextMonthButtonXpath.formatted(dynamicValue));
    }

    private void navigateToTargetMonth(YearMonth yearMonth, String dynamicValue) {
        SelenideElement prevButton = getPrevMonthButton(dynamicValue);
        SelenideElement nextButton = getNextMonthButton(dynamicValue);

        YearMonth currentMonth = YearMonth.parse($x(currentMonthXpath.formatted(dynamicValue))
                .$x(monthDisplayValueAdditionalXpath).getText() );

        while (currentMonth.compareTo(yearMonth) != 0) {
            if (currentMonth.isAfter(yearMonth)) {
                scrollToElement(prevButton);
                prevButton.click();
            } else {
                scrollToElement(nextButton);
                nextButton.click();
            }
        }
    }

    private YearMonth findLowestPriceMonth(LocalDate startDate, LocalDate endDate, String dynamicValue) {
        ElementsCollection monthList = $$x(monthListXpath.formatted(dynamicValue));
        YearMonth startMonth = YearMonth.from(startDate);
        YearMonth endMonth = YearMonth.from(endDate);

        SelenideElement lowestMonth = monthList.get(0);
        int lowestPrice = NumberHelper.parsePrice(lowestMonth.$x(monthPriceAdditionalXpath).getText());

        for (SelenideElement month : monthList) {
            String monthText = month.$x(monthDisplayValueAdditionalXpath).getText();
            YearMonth currentMonth = YearMonth.parse(monthText);

            if (currentMonth.isAfter(startMonth) || currentMonth.isBefore(endMonth)) {
                SelenideElement priceElement = month.$x(monthPriceAdditionalXpath);
                if (priceElement.exists()) {
                    int currentPrice = NumberHelper.parsePrice(priceElement.getText());
                    if (currentPrice < lowestPrice) {
                        lowestPrice = currentPrice;
                        lowestMonth = month;
                    }
                }
            } else {
                // Skip months outside the specified range
                break;
            }
        }

        return YearMonth.parse(lowestMonth.$x(monthDisplayValueAdditionalXpath).getText());
    }

    private void selectLowestPriceMonth(LocalDate startDate, LocalDate endDate, String dynamicValue) {
        // Navigate to the start month
        YearMonth startYearMonth = YearMonth.from(startDate);
        navigateToTargetMonth(startYearMonth, dynamicValue);

        // Find the month with the lowest price
        YearMonth lowestPriceYearMonth = findLowestPriceMonth(startDate, endDate, dynamicValue);

        // Navigate to the lowest month
        navigateToTargetMonth(lowestPriceYearMonth, dynamicValue);
    }

    private void selectCheapestFlightTicket(String dynamicValue) {
        ElementsCollection availableTicketCollection = $$x(availableTicketListXpath.formatted(dynamicValue));
        if (availableTicketCollection.isEmpty()) {
            throw new AssertionError("No available tickets found.");
        }

        SelenideElement cheapestTicket = availableTicketCollection.get(0);
        int lowestPrice = NumberHelper.parsePrice(cheapestTicket.$x(ticketPriceAdditionalXpath).getText());

        for (SelenideElement ticket : availableTicketCollection) {
            scrollToElement(ticket);
            int currentPrice = NumberHelper.parsePrice(ticket.$x(ticketPriceAdditionalXpath).getText());
            if (currentPrice < lowestPrice) {
                cheapestTicket = ticket;
                lowestPrice = currentPrice;
            }
        }

        cheapestTicket.click();
    }

    private void selectLowestMonthFlight(LocalDate startDate, LocalDate endDate, String dynamicValue) {
        selectLowestPriceMonth(startDate, endDate, dynamicValue);
        selectCheapestFlightTicket(dynamicValue);
    }

    private void clickContinueButton() {
        clickWhenReady(continueButton);
    }

    //need to update this method to choose the return flight is three days after the departure flight
    public void selectMonthFlight(int departAfterMonths, int returnAfterMonths) {
        LocalDate departLocalDate = LocalDate.now().plusMonths(departAfterMonths);
        LocalDate returnLocalDate = departLocalDate.plusMonths(returnAfterMonths);
        selectLowestMonthFlight(departLocalDate, returnLocalDate, LanguageManager.get("departure_flight"));

        selectLowestMonthFlight(departLocalDate, returnLocalDate, LanguageManager.get("return_flight"));

        clickContinueButton();
    }

    public void verifySelectFlightCheapPageDisplayed() {
        //check the url
        webdriver().shouldHave(urlContaining("/select-flight-cheap"));
    }
}
