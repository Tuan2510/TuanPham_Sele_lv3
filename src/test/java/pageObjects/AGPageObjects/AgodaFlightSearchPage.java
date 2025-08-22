package pageObjects.AGPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testDataObject.AGTest.FlightDisplayInfo;
import utils.LanguageManager;
import utils.LogHelper;
import utils.TestListener;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.ValueHelper.parseDurationToMinutes;

public class AgodaFlightSearchPage {
    private static final Logger logger = LoggerFactory.getLogger(AgodaFlightSearchPage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    // Locators
    private final SelenideElement sortFlightsButton = $("//div[@data-element-name='flight-sort']//button");
    private final SelenideElement addToCartButton = $("[data-element-name='flights-details-add-to-cart']"); // TODO
    private final SelenideElement cardPanel = $("[data-element-name='cart-interstitial']"); // TODO
    private final SelenideElement proceedToCartButton = $("[data-element-name='proceed-to-cart']"); // TODO

    private final SelenideElement flightDate = $("[data-selenium='cart-flight-date']"); // TODO
    private final SelenideElement flightTimes = $("[data-selenium='cart-flight-times']"); // TODO
    private final SelenideElement airlineAndClass = $("[data-selenium='cart-airline-class']"); // TODO

    private final ElementsCollection flightDurationsCollection = $$("[data-testid='duration']"); // TODO

    // Dynamic locators
    private final String sortByItem = "//span[contains(text(), '%s')]";




    public void verifyPageIsDisplayed() {
        webdriver().shouldHave(urlContaining("flights/results?"));
        logHelper.logStep("Search results page is displayed with URL: %s", webdriver().driver().getCurrentFrameUrl());
    }


    public void applySortByFastest() {
        logHelper.logStep("Applying sort by fastest flights");
        sortFlightsButton.hover().click();

        SelenideElement sortByFastest = $x(String.format(sortByItem, LanguageManager.get("sort_by_fastest")));
        sortByFastest.shouldBe(Condition.visible).click();
    }

    public void verifyResultsSortedByFastest(int numberOfFlights) {
        logHelper.logStep("Verify that first " + numberOfFlights + " flight durations are sorted by fastest");

        List<String> flightDurations = flightDurationsCollection.texts();

        // Limit to the smaller of numberOfFlights or actual size
        int limit = Math.min(numberOfFlights, flightDurations.size());

        for (int i = 0; i < limit - 1; i++) {
            int duration1InMinutes = parseDurationToMinutes(flightDurations.get(i));
            int duration2InMinutes = parseDurationToMinutes(flightDurations.get(i + 1));

            if (duration1InMinutes > duration2InMinutes) {
                throw new AssertionError("Flight durations are not sorted by fastest within the first " + limit + " flights");
            }
        }
    }

    public void verifyFlightInfo(FlightDisplayInfo expected) {
        logHelper.logStep("Verify flight info in cart");
        LocalDate actualDate = LocalDate.parse(flightDate.getText(), DateTimeFormatter.ofPattern("yyyy-MM-dd")); // TODO pattern
        String[] times = flightTimes.getText().split("-");
        LocalTime actualDeparture = LocalTime.parse(times[0].trim(), DateTimeFormatter.ofPattern("HH:mm")); // TODO pattern
        LocalTime actualArrival = LocalTime.parse(times[1].trim(), DateTimeFormatter.ofPattern("HH:mm")); // TODO pattern
        String airlineClassText = airlineAndClass.getText();

        if (!actualDate.equals(expected.getDate())) {
            throw new AssertionError("Flight date mismatch");
        }
        if (!actualDeparture.equals(expected.getDepartureTime()) || !actualArrival.equals(expected.getArrivalTime())) {
            throw new AssertionError("Flight time mismatch");
        }
        if (!airlineClassText.contains(expected.getAirline()) || !airlineClassText.contains(expected.getFlightClass())) {
            throw new AssertionError("Flight airline or class mismatch");
        }
    }


    public void expandFlightByIndex(int i) {
        logHelper.logStep("Selecting flight by index: " + i);
        ElementsCollection flightCards = $$("[data-testid='flight-card']");

        if (i < 1 || i > flightCards.size()) {
            throw new IndexOutOfBoundsException("Flight index out of bounds: " + i);
        }

        SelenideElement selectedFlight = flightCards.get(i - 1);
        selectedFlight.scrollIntoView(true).shouldBe(Condition.visible).click();
    }

    public void addFlightToCart() {
        logHelper.logStep("Adding flight to cart");
        addToCartButton.shouldBe(Condition.visible).click();
        // Wait for the cart panel to appear
        cardPanel.shouldBe(Condition.visible, Duration.ofSeconds(5));
    }

    private void proceedToCart() {
        logHelper.logStep("Proceeding to cart");
        proceedToCartButton.shouldBe(Condition.visible).click();
        // Wait for the cart page to load
        SelenideElement cartPage = $("[data-selenium='cart-page']"); // TODO locator
        cartPage.shouldBe(Condition.visible);
        logHelper.logStep("Navigated to cart page successfully");
    }
}
