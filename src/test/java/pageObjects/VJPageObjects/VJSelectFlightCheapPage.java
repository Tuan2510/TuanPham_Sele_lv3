package pageObjects.VJPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.StaleElementReferenceException;
import utils.LanguageManager;
import utils.NumberHelper;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.DateHelper.parseYearMonth;
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
    private final String monthDisplayValueSelector = "div p.MuiTypography-h6";
    private final String monthPriceSelector = "div p span:first-child";
    private final String ticketDateSelector = "p";
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

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.US);
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
    }

    private void waitForMonthToLoad(String dynamicValue) {
        ElementsCollection availableTicketCollection = $$x(availableTicketListXpath.formatted(dynamicValue));
        availableTicketCollection.shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
    }

    private YearMonth findLowestPriceMonth(YearMonth startYearMonth, YearMonth endYearMonth, String dynamicValue) {
        // ensure month carousel is at start of range
        navigateToTargetMonth(startYearMonth, dynamicValue);

        YearMonth lowestMonth = startYearMonth;
        int lowestPrice = Integer.MAX_VALUE;
        YearMonth currentMonth = startYearMonth;

        while (!currentMonth.isAfter(endYearMonth)) {
            for (int attempt = 0; attempt < 2; attempt++) {
                try {
                    waitForMonthToLoad(dynamicValue);
                    SelenideElement monthElement = $x(currentMonthXpath.formatted(dynamicValue));

                    currentMonth = parseYearMonth(monthElement.$(monthDisplayValueSelector).getText());
                    int currentPrice = NumberHelper.parsePrice(monthElement.$(monthPriceSelector)
                            .shouldBe(Condition.visible).getText());
                    if (currentPrice < lowestPrice) {
                        lowestPrice = currentPrice;
                        lowestMonth = currentMonth;
                    }
                    break;
                } catch (StaleElementReferenceException e) {
                    //retry
                }
            }
            if (currentMonth.equals(endYearMonth)) {
                break;
            }
            clickWhenReady(getNextMonthButton(dynamicValue));
            currentMonth = currentMonth.plusMonths(1);
        }

        return lowestMonth;
    }

    private SelenideElement findCheapestFlightTicket(String dynamicValue) {
        waitForMonthToLoad(dynamicValue);
        ElementsCollection ticketCollection = $$x(availableTicketListXpath.formatted(dynamicValue));
        ticketCollection.shouldHave(sizeGreaterThan(0));

        SelenideElement cheapestTicket = ticketCollection.first();
        int lowestPrice = NumberHelper.parsePrice(cheapestTicket.$x(ticketPriceAdditionalXpath).getText());

        for (SelenideElement ticket : ticketCollection) {
            scrollToElement(ticket);
            int currentPrice = NumberHelper.parsePrice(ticket.$x(ticketPriceAdditionalXpath).getText());
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
        clickWhenReady(cheapestTicket);
    }

    //need to update this method to choose the return flight is three days after the departure flight
    public void selectMonthFlight(int departAfterMonths, int returnAfterMonths, int returnFlightAfterDays) {
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

        //navigate to the same month for return flight
        navigateToTargetMonth(lowestYearMonth, LanguageManager.get("return_flight"));

        int departDay = Integer.parseInt(cheapestDepartTicket.$(ticketDateSelector).getText().trim());
        LocalDate returnDate = lowestYearMonth.atDay(departDay).plusDays(returnFlightAfterDays);

        waitForMonthToLoad(LanguageManager.get("return_flight"));

        // choose the return flight ticket
        ElementsCollection returnTicketList = $$x(availableTicketListXpath.formatted(LanguageManager.get("return_flight")));
        SelenideElement returnTicket = returnTicketList.find(text(String.valueOf(returnDate.getDayOfMonth())));
        selectFlight(lowestYearMonth, LanguageManager.get("return_flight"), returnTicket);
    }

    public void verifySelectFlightCheapPageDisplayed() {
        //check the url
        webdriver().shouldHave(urlContaining("/select-flight-cheap"));
    }

    public void clickContinueButton() {
        clickWhenReady(continueButton);
    }
}
