package pageObjects.VJPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.StaleElementReferenceException;
import utils.LanguageManager;
import utils.NumberHelper;
import testDataObject.VJTest.CheapestTicketDate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.sleep;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.DateHelper.parseYearMonth;
import static utils.ElementHelper.clickWhenReady;
import static utils.ElementHelper.isElementDisplayed;
import static utils.ElementHelper.scrollToElement;
import static utils.ElementHelper.switchToDefault;
import static utils.ElementHelper.switchToIframe;

public class VJSelectFlightCheapPage {
    // Locators
    private final SelenideElement alertOfferIframe = $("#preview-notification-frame");
    private final SelenideElement alertOfferLaterBtn = $("#NC_CTA_TWO");
    private final SelenideElement closeAdPanelButton = $("button.MuiButtonBase-root[aria-label='close']");
    private final SelenideElement continueButton = $("div.MuiBox-root > button.MuiButtonBase-root");
    private final SelenideElement selectingSampleColorElement = $x(String.format("//div[h5[text()='%s']]/div[2]", LanguageManager.get("selecting")));

    // Dynamic Locators
    private final String prevMonthButtonXpath = "//div[div/p[contains(text(), '%s')]]//div[@class='slick-slider slick-initialized']//button[1]";
    private final String nextMonthButtonXpath = "//div[div/p[contains(text(), '%s')]]//div[@class='slick-slider slick-initialized']//button[2]";
    private final String monthListXpath = "//div[div/p[contains(text(), '%s')]]//div/div/div[contains(@class, 'slick-slide')]";
    private final String currentMonthXpath = "//div[div/p[contains(text(), '%s')]]//div[contains(@class, 'slick-current')]";
    private final String availableTicketListXpath = "//div[div/p[contains(text(), '%s')]]//div[@role='button'][.//div/span]";

    //Additional Locators
    private final String monthDisplayValueSelector = "div p.MuiTypography-h6";
    private final String monthPriceSelector = "div p span:first-child";
    private final String ticketDateSelector = "p";
    private final String ticketPriceAdditionalXpath = ".//span[not(contains(normalize-space(), 'VND'))]";

    // Methods
    @Step("Close offer alert if displayed")
    public void closeOfferAlert() {
        if (alertOfferIframe.isDisplayed()) {
            switchToIframe(alertOfferIframe);

            if (alertOfferLaterBtn.isDisplayed()) {
                alertOfferLaterBtn.click();
            }

            switchToDefault();
        }
    }

    @Step("Select flight type")
    public void closeAdPanelButton(){
        if (isElementDisplayed(closeAdPanelButton) ) {
            clickWhenReady(closeAdPanelButton);
        }
    }


    private SelenideElement getPrevMonthButton(String dynamicValue) {
        return $x(prevMonthButtonXpath.formatted(dynamicValue));
    }

    private SelenideElement getNextMonthButton(String dynamicValue) {
        return $x(nextMonthButtonXpath.formatted(dynamicValue));
    }

    private void navigateToTargetMonth(YearMonth yearMonth, String dynamicValue) {
        SelenideElement prevButton = getPrevMonthButton(dynamicValue);
        SelenideElement nextButton = getNextMonthButton(dynamicValue);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy", LanguageManager.getLocale());
        YearMonth currentMonth = YearMonth.parse($x(currentMonthXpath.formatted(dynamicValue))
                .$(monthDisplayValueSelector).getText(), formatter );

        while (currentMonth.compareTo(yearMonth) != 0) {
            waitForMonthToLoad(dynamicValue);
            if (currentMonth.isAfter(yearMonth)) {
                scrollToElement(prevButton);
                prevButton.click();
            } else {
                scrollToElement(nextButton);
                nextButton.click();
            }
            // Update currentMonth after clicking
            currentMonth = YearMonth.parse($x(currentMonthXpath.formatted(dynamicValue))
                    .$(monthDisplayValueSelector).getText(), formatter);
        }
        //verify current month is displayed
        $x(currentMonthXpath.formatted(dynamicValue))
                .$(monthDisplayValueSelector).shouldHave(Condition.text(yearMonth.format(formatter)));

    }

    private void waitForMonthToLoad(String dynamicValue) {
        ElementsCollection availableTicketCollection = $$x(availableTicketListXpath.formatted(dynamicValue));
        availableTicketCollection.shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
    }

    private YearMonth findLowestPriceMonth(YearMonth startYearMonth, YearMonth endYearMonth, String dynamicValue) {
        // Navigate to the starting month
        navigateToTargetMonth(startYearMonth, dynamicValue);

        // Initialize variables to track the lowest price and corresponding month
        YearMonth lowestMonth = startYearMonth;
        int lowestPrice = Integer.MAX_VALUE;
        YearMonth currentMonth = startYearMonth;

        // loop through months until we reach the end month to find the lowest price
        while (!currentMonth.isAfter(endYearMonth)) {
            // Ensure month content has loaded
            waitForMonthToLoad(dynamicValue);

            // Get the month element and extract the month text and price
            SelenideElement monthElement = $x(currentMonthXpath.formatted(dynamicValue))
                    .should(Condition.exist);
            String monthText = monthElement.$(monthDisplayValueSelector).text();
            String priceText = monthElement.$(monthPriceSelector).shouldBe(Condition.visible).text();
            YearMonth parsedMonth = parseYearMonth(monthText);
            int currentPrice = NumberHelper.parsePrice(priceText);

            // If the current month is not valid, skip it
            if (currentPrice < lowestPrice) {
                lowestPrice = currentPrice;
                lowestMonth = parsedMonth;
            }

            // If we reach the end month, break the loop
            if (parsedMonth.equals(endYearMonth)) {
                break;
            }
            clickWhenReady(getNextMonthButton(dynamicValue));
            currentMonth = currentMonth.plusMonths(1);
        }

        return lowestMonth;
    }

    private SelenideElement findCheapestFlightTicket(String dynamicValue) {
        // Ensure month content has loaded
        waitForMonthToLoad(dynamicValue);

        // Get all available tickets (must be at least one)
        ElementsCollection ticketCollection = $$x(availableTicketListXpath.formatted(dynamicValue));
        ticketCollection.shouldHave(sizeGreaterThan(0));

        // Initialize with the first ticket as the cheapest
        SelenideElement cheapestTicket = ticketCollection.first();
        int lowestPrice = NumberHelper.parsePrice(
                cheapestTicket.$x(ticketPriceAdditionalXpath).getText()
        );

        // Iterate through all tickets to find the cheapest one
        for (int i = 0; i < ticketCollection.size(); i++) {
            // Access ticket, scroll to it and extract price
            SelenideElement ticket = ticketCollection.get(i);
            scrollToElement(ticket);
            int currentPrice = NumberHelper.parsePrice(
                    ticket.$x(ticketPriceAdditionalXpath).getText()
            );

            // Update cheapest if lower
            if (currentPrice < lowestPrice) {
                cheapestTicket = ticket;
                lowestPrice = currentPrice;
            }
        }

        return cheapestTicket;
    }

    private void selectFlight(YearMonth lowestYearMonth, String dynamicValue, SelenideElement cheapestTicket) {
        //find the lowest price month and navigate to it
        navigateToTargetMonth(lowestYearMonth, dynamicValue);

        //select the cheapest flight ticket
        scrollToElement(cheapestTicket);
        sleep(1000);
        clickWhenReady(cheapestTicket);
        sleep(1000);
        String bgColor = selectingSampleColorElement.getCssValue("background-color");
        cheapestTicket.shouldHave(Condition.cssValue("background-color", bgColor),
                Duration.ofSeconds(10));
    }

    public CheapestTicketDate selectCheapestTicketDates(int departAfterMonths, int returnAfterMonths, int returnFlightAfterDays) {
        closeOfferAlert();
        closeAdPanelButton();

        CheapestTicketDate ticketDate = new CheapestTicketDate();
        LocalDate departLocalDate = LocalDate.now().plusMonths(departAfterMonths);
        LocalDate returnLocalDate = departLocalDate.plusMonths(returnAfterMonths);

        YearMonth startYearMonth = YearMonth.from(departLocalDate);
        YearMonth endYearMonth = YearMonth.from(returnLocalDate);

        //find the lowest price month and navigate to it
        YearMonth lowestYearMonth =  findLowestPriceMonth(startYearMonth, endYearMonth, LanguageManager.get("departure_flight"));
        navigateToTargetMonth(lowestYearMonth, LanguageManager.get("departure_flight"));

        //select the cheapest flight ticket in the departure month
        SelenideElement cheapestDepartTicket = findCheapestFlightTicket(LanguageManager.get("departure_flight"));
        selectFlight(lowestYearMonth, LanguageManager.get("departure_flight"), cheapestDepartTicket);
        int departDay = Integer.parseInt(cheapestDepartTicket.$(ticketDateSelector).getText().trim());
        LocalDate departDate = lowestYearMonth.atDay(departDay);
        ticketDate.setDepartDate(departDate);

        //navigate to the same month for return flight
        navigateToTargetMonth(lowestYearMonth, LanguageManager.get("return_flight"));

        LocalDate returnDate = departDate.plusDays(returnFlightAfterDays);
        waitForMonthToLoad(LanguageManager.get("return_flight"));

        // choose the return flight ticket
        ElementsCollection returnTicketList = $$x(availableTicketListXpath.formatted(LanguageManager.get("return_flight")));
        SelenideElement returnTicket = returnTicketList.find(text(String.valueOf(returnDate.getDayOfMonth())));
        selectFlight(lowestYearMonth, LanguageManager.get("return_flight"), returnTicket);
        ticketDate.setReturnDate(returnDate);
        return ticketDate;
    }

    public void verifySelectFlightCheapPageDisplayed() {
        //check the url
        webdriver().shouldHave(urlContaining("/select-flight-cheap"));
    }

    public void clickContinueButton() {
        clickWhenReady(continueButton);
    }
}
