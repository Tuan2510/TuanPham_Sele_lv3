package pageObjects.AGPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testDataObject.AGTest.FlightClass;
import testDataObject.AGTest.FlightOccupancy;
import testDataObject.AGTest.Occupancy;
import utils.DatePickerHelper;
import utils.LanguageManager;
import utils.LogHelper;
import utils.TestListener;

import java.time.Duration;
import java.time.LocalDate;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class AgodaHomePage {
    private static final Logger logger = LoggerFactory.getLogger(AgodaHomePage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    // Static Locators
    private final SelenideElement menuIcon = $("button.igBcCj");
    private final SelenideElement menuWindow = $("div[data-selenium='hamburger-menu-dropdown-container']");
    private final SelenideElement currencySelector = $("[data-selenium='currency-container-selected-currency-name']");
    private final SelenideElement currencyVND = $x("//p[text()='Vietnamese Dong']");
    private final SelenideElement closeAdButton = $("[data-element-name='prominent-app-download-floating-button']");

    //Hotel tab locators
    private final SelenideElement destinationInput = $("[data-selenium='textInput']");
    private final SelenideElement checkInToggle = $("[data-element-name='check-in-box']");
    private final SelenideElement checkOutToggle = $("[data-element-name='check-out-box']");
    private final SelenideElement occupancyToggle = $("[data-element-name='occupancy-box']");

    // Flight tab locators
    private final SelenideElement tabFlight = $("#tab-flight-tab");
    private final SelenideElement oneWayFlightButton = $("button[label='One-way']");
    private final SelenideElement flightOriginInput = $("[data-selenium='flight-origin-search-input']");
    private final SelenideElement flightDestinationInput = $("[data-selenium='flight-destination-search-input']");
    private final SelenideElement flightDateToggle = $("[data-selenium='flight-date-selector']");
    private final SelenideElement flightOccupancyToggle = $("[data-element-name='flight-occupancy']");


    // Date Picker locators
    private final SelenideElement datePickerWindow = $("[data-selenium='rangePickerCheckIn'], [data-selenium='range-picker-date']");
    private final SelenideElement datePickerCaption = $("div.DayPicker-Caption");
    private final SelenideElement prevMonthButton = $("[aria-label='Previous month' i]");
    private final SelenideElement nextMonthButton = $("[aria-label='Next month' i]");

    private final SelenideElement searchButton = $("[data-selenium='searchButton']");

    // Dynamic Locators
    private final String selectableDate = "[data-selenium-date='%s']";
    private final String searchSuggestion = "[data-text='%s']";
    private final String occupancyDisplayValue = "//div[div/h2[contains(text(), '%s')]]//div[@data-selenium]/p";
    private final String occupancyMinusButton = "//div[div/*[contains(text(), '%s')]]//button[@data-selenium='minus']";
    private final String occupancyPlusButton = "//div[div/*[contains(text(), '%s')]]//button[@data-selenium='plus']";

    private final String flightOccupancyDisplayValue = "//div[div/*[contains(text(), '%s')]]//span[@data-selenium]";
    private final String flightClassButton = "[data-element-object-id='%s']";

    //Methods
    private void openMenu() {
        if(!menuWindow.isDisplayed()) {
            menuIcon.click();
        }
    }

    private void closeMenu() {
        if(menuWindow.isDisplayed()) {
            menuIcon.click();
        }
    }

    private void selectVNDCurrency() {
        currencySelector.click();
        currencyVND.click();
    }

    private void changeLanguageAndCurrencyToVND() {
        closeAdButton.click(); // Close the ad if it appears
        openMenu();
        selectVNDCurrency();
        closeMenu();
    }

    private void setDestination(String place) {
        destinationInput.click();
        destinationInput.setValue(place).pressEnter();
        // Wait for suggestions to appear and select the first one
        SelenideElement firstSuggestion = $(String.format(searchSuggestion, place));
        firstSuggestion.shouldBe(Condition.visible).click();
    }

    private void selectOneWayFlight() {
        if (!oneWayFlightButton.isDisplayed()) {
            tabFlight.click();
        }
        oneWayFlightButton.click();
    }

    private void setFlightLocation(SelenideElement input, String location) {
        input.click();
        input.setValue(location).pressEnter();
        // Wait for suggestions to appear and select the first one
        SelenideElement firstSuggestion = $(String.format(searchSuggestion, location));
        firstSuggestion.shouldBe(Condition.visible).click();
    }

    private void setTravelDate(LocalDate checkIn, LocalDate checkOut) {
        datePickerWindow.shouldBe(Condition.visible, Duration.ofSeconds(10));
        selectTravelDate(checkIn);
        selectTravelDate(checkOut);
    }

    private void setOneWayFlightDate(LocalDate localDate) {
        datePickerWindow.shouldBe(Condition.visible, Duration.ofSeconds(10));
        selectTravelDate(localDate);
    }

    private void selectTravelDate(LocalDate date) {
        if(!datePickerCaption.isDisplayed()){
            flightDateToggle.click();
        }

        DatePickerHelper datePickerHelper = new DatePickerHelper(
                $(datePickerCaption),
                $(nextMonthButton),
                $(prevMonthButton),
                selectableDate
        );
        datePickerHelper.selectDateCss(date);
    }

    private void setOccupancy(Occupancy occupancy) {
        int currentRooms = Integer.parseInt($x(String.format(occupancyDisplayValue, LanguageManager.get("room"))).getText().trim());
        int currentAdults = Integer.parseInt($x(String.format(occupancyDisplayValue, LanguageManager.get("adults"))).getText().trim());
        int currentChildren = Integer.parseInt($x(String.format(occupancyDisplayValue, LanguageManager.get("children"))).getText().trim());

        adjustOccupancyValue(LanguageManager.get("room"), currentRooms, occupancy.getRoomCount());
        adjustOccupancyValue(LanguageManager.get("adults"), currentAdults, occupancy.getAdultCount());
        adjustOccupancyValue(LanguageManager.get("children"), currentChildren, occupancy.getChildCount());
    }

    private void setFlightOccupancy(FlightOccupancy flightOccupancy) {
        int currentAdults = Integer.parseInt($x(String.format(flightOccupancyDisplayValue, LanguageManager.get("adults"))).getText().trim());
        int currentChildren = Integer.parseInt($x(String.format(flightOccupancyDisplayValue, LanguageManager.get("children"))).getText().trim());
        int currentInfants = Integer.parseInt($x(String.format(flightOccupancyDisplayValue, LanguageManager.get("infants"))).getText().trim());

        adjustOccupancyValue(LanguageManager.get("adults"), currentAdults, flightOccupancy.getAdultCount());
        adjustOccupancyValue(LanguageManager.get("children"), currentChildren, flightOccupancy.getChildCount());
        adjustOccupancyValue(LanguageManager.get("infants"), currentInfants, flightOccupancy.getInfantCount());
    }

    private void adjustOccupancyValue(String type, int current, int target) {
        if (current < target) {
            for (int i = current; i < target; i++) {
                $x(occupancyPlusButton.formatted(type)).click();
            }
        } else if (current > target) {
            for (int i = current; i > target; i--) {
                $x(occupancyMinusButton.formatted(type)).click();
            }
        }
    }

    private void selectFlightClass(FlightClass flightClass) {
        SelenideElement flightClassButton = $(String.format(this.flightClassButton, flightClass.getFlightClass()));
        flightClassButton.click();
    }

    /**
     * Searches for a hotel with the specified parameters.
     *
     * @param place         The destination place to search for hotels.
     * @param checkInDate   The check-in date.
     * @param checkOutDate  The check-out date.
     * @param occupancy     The occupancy details including room count, adult count, and child count.
     */
    public void searchHotel(String place, LocalDate checkInDate, LocalDate checkOutDate, Occupancy occupancy) {
        logHelper.logStep("Change currency to VND");
        changeLanguageAndCurrencyToVND();

        logHelper.logStep("Set destination: %s", place);
        setDestination(place);

        logHelper.logStep("Set check-in date: %s, check-out date: %s", checkInDate, checkOutDate);
        setTravelDate(checkInDate, checkOutDate);

        logHelper.logStep("Set occupancy: %d rooms, %d adults, %d children",
                occupancy.getRoomCount(), occupancy.getAdultCount(), occupancy.getChildCount());
        setOccupancy(occupancy);

        logHelper.logStep("Click search button");
        searchButton.click();
    }

    /**
     * Navigates to the flight page.
     */
    public void goToFlightPage() {
        tabFlight.click();
    }

    /**
     * Searches for a one-way flight with the specified parameters.
     *
     * @param origin        The origin location for the flight.
     * @param destination   The destination location for the flight.
     * @param departureDate The departure date for the flight.
     * @param flightClass   The class of the flight (e.g., Economy, Premium Economy).
     * @param flightOccupancy The occupancy details including adult count, child count, and infant count.
     */
    public void searchOneWayFlight(String origin, String destination, LocalDate departureDate, FlightClass flightClass, FlightOccupancy flightOccupancy) {
        logHelper.logStep("Change currency to VND");
        changeLanguageAndCurrencyToVND();

        logHelper.logStep("Set flight type to One Way");
        selectOneWayFlight();

        logHelper.logStep("Set flight origin: %s", origin);
        setFlightLocation(flightOriginInput, origin);

        logHelper.logStep("Set flight destination: %s", destination);
        setFlightLocation(flightDestinationInput, destination);

        logHelper.logStep("Set flight date");
        setOneWayFlightDate(departureDate);

        logHelper.logStep("Set flight occupancy: %s", flightOccupancy.toString());
        setFlightOccupancy(flightOccupancy);

        logHelper.logStep("Select flight class as Premium Economy");
        selectFlightClass(flightClass);

        logHelper.logStep("Click search button");
        searchButton.click();
    }
}
