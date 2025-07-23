package pageObjects.VJPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import utils.LanguageManager;
import utils.NumberHelper;
import testDataObject.VJTest.CheapestTicketDate;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
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
    private final String prevMonthButtonXpath = "//div[div/p[contains(text(), '%s')]]//button[1]";
    private final String nextMonthButtonXpath = "//div[div/p[contains(text(), '%s')]]//button[2]";
    private final String currentMonthXpath = "//div[div/p[contains(text(), '%s')]]//div[contains(@class, 'slick-current')]";
    private final String availableTicketListXpath = "//div[div/p[contains(text(), '%s')]]//div[@role='button'][.//div/span]";

    //Additional Locators
    private final String monthDisplayValueSelector = "div p.MuiTypography-h6";
    private final String monthPriceSelector = "div p span:first-child";
    private final String ticketDateSelector = "p";
    private final String ticketPriceAdditionalXpath = ".//span[not(contains(normalize-space(), 'VND'))]";

    // Methods
    /**
     * Closes the offer alert if it is displayed.
     * This method switches to the iframe containing the alert, clicks the "Later" button if it is visible,
     * and then switches back to the default content.
     */
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

    /**
     * Closes the advertisement panel if it is displayed.
     * This method checks if the close button for the ad panel is visible and clicks it to close the panel.
     */
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

    private ElementsCollection getAvailableTickets(String dynamicValue) {
        waitForMonthToLoad(dynamicValue);
        return $$x(availableTicketListXpath.formatted(dynamicValue))
                .shouldHave(sizeGreaterThan(0));
    }

    private int getTicketPrice(SelenideElement ticket) {
        return NumberHelper.parsePrice(ticket.$x(ticketPriceAdditionalXpath).getText());
    }

    private int getTicketDay(SelenideElement ticket) {
        return Integer.parseInt(ticket.$(ticketDateSelector).getText().trim());
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

    private void selectFlight(YearMonth lowestYearMonth, String dynamicValue, SelenideElement cheapestTicket) {
        //find the lowest price month and navigate to it
        navigateToTargetMonth(lowestYearMonth, dynamicValue);

        //select the cheapest flight ticket
        scrollToElement(cheapestTicket);
        clickWhenReady(cheapestTicket);
        String bgColor = selectingSampleColorElement.getCssValue("background-color");
        cheapestTicket.shouldHave(Condition.cssValue("background-color", bgColor),
                Duration.ofSeconds(10));
    }

    private SelenideElement findTicketByDay(int day, String dynamicValue) {
        return getAvailableTickets(dynamicValue).find(text(String.valueOf(day)));
    }

    private CheapestTicketDate findLowestTotalPriceTrip(YearMonth startYearMonth, YearMonth endYearMonth, int returnAfterDays)  {
        CheapestTicketDate result = new CheapestTicketDate();
        int lowestTotal = Integer.MAX_VALUE;
        LocalDate bestDepart = null;
        LocalDate bestReturn = null;

        YearMonth currentMonth = startYearMonth;
        while (!currentMonth.isAfter(endYearMonth)) {
            navigateToTargetMonth(currentMonth, LanguageManager.get("departure_flight"));

            ElementsCollection departTickets = getAvailableTickets(LanguageManager.get("departure_flight"));

            for (int i = 0; i < departTickets.size(); i++) {
                SelenideElement departTicket = departTickets.get(i);
                int departPrice = getTicketPrice(departTicket);
                int departDay = getTicketDay(departTicket);
                LocalDate departDate = currentMonth.atDay(departDay);

                LocalDate returnDate = departDate.plusDays(returnAfterDays);
                YearMonth returnMonth = YearMonth.from(returnDate);

                navigateToTargetMonth(returnMonth, LanguageManager.get("return_flight"));
                SelenideElement returnTicket = findTicketByDay(returnDate.getDayOfMonth(), LanguageManager.get("return_flight"));
                if (returnTicket == null || !returnTicket.exists()) {
                    navigateToTargetMonth(currentMonth, LanguageManager.get("departure_flight"));
                    continue;
                }
                int returnPrice = getTicketPrice(returnTicket);
                int total = departPrice + returnPrice;

                if (total < lowestTotal) {
                    lowestTotal = total;
                    bestDepart = departDate;
                    bestReturn = returnDate;
                }

                navigateToTargetMonth(currentMonth, LanguageManager.get("departure_flight"));
                departTickets = getAvailableTickets(LanguageManager.get("departure_flight"));
            }

            currentMonth = currentMonth.plusMonths(1);
        }

        if (bestDepart != null) {
            result.setDepartDate(bestDepart);
            result.setReturnDate(bestReturn);
        }
        return result;
    }

    /**
     * Selects the cheapest ticket dates based on the specified parameters.
     * This method navigates to the target months, finds the cheapest tickets for departure and return flights,
     * and returns the best dates found.
     *
     * @param departAfterMonths The number of months after which to start looking for departure flights.
     * @param returnAfterMonths The number of months after which to start looking for return flights.
     * @param returnFlightAfterDays The number of days after the departure flight to look for return flights.
     * @return CheapestTicketDate object containing the best departure and return dates found.
     */
    public CheapestTicketDate selectCheapestTicketDates(int departAfterMonths, int returnAfterMonths, int returnFlightAfterDays) {
        closeOfferAlert();
        closeAdPanelButton();

        LocalDate startDate = LocalDate.now().plusMonths(departAfterMonths);
        LocalDate endDate = startDate.plusMonths(returnAfterMonths);

        YearMonth startYearMonth = YearMonth.from(startDate);
        YearMonth endYearMonth = YearMonth.from(endDate);

        navigateToTargetMonth(startYearMonth, LanguageManager.get("departure_flight"));

        CheapestTicketDate bestDates = findLowestTotalPriceTrip(startYearMonth, endYearMonth, returnFlightAfterDays);

        navigateToTargetMonth(YearMonth.from(bestDates.getDepartDate()), LanguageManager.get("departure_flight"));
        SelenideElement departTicket = findTicketByDay(bestDates.getDepartDate().getDayOfMonth(), LanguageManager.get("departure_flight"));
        selectFlight(YearMonth.from(bestDates.getDepartDate()), LanguageManager.get("departure_flight"), departTicket);

        navigateToTargetMonth(YearMonth.from(bestDates.getReturnDate()), LanguageManager.get("return_flight"));
        SelenideElement returnTicket = findTicketByDay(bestDates.getReturnDate().getDayOfMonth(), LanguageManager.get("return_flight"));
        selectFlight(YearMonth.from(bestDates.getReturnDate()), LanguageManager.get("return_flight"), returnTicket);

        return bestDates;
    }

    /**
     * Verifies that the Select Flight Cheap page is displayed.
     * This method checks the current URL to ensure it contains the expected path for the Select Flight Cheap page.
     */
    public void verifySelectFlightCheapPageDisplayed() {
        //check the url
        webdriver().shouldHave(urlContaining("/select-flight-cheap"));
    }

    /**
     * Clicks the continue button to proceed with the flight selection.
     * This method waits for the button to be ready and then clicks it.
     */
    public void clickContinueButton() {
        clickWhenReady(continueButton);
    }
}
