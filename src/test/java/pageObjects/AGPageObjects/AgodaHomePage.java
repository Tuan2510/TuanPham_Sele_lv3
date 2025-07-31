package pageObjects.AGPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.DatePickerHelper;
import utils.ElementHelper;
import utils.LanguageManager;
import utils.LogHelper;
import utils.TestListener;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

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

    private final SelenideElement destinationInput = $("[data-selenium='textInput']");
    private final SelenideElement checkInToggle = $("[data-element-name='check-in-box']");
    private final SelenideElement checkOutToggle = $("[data-element-name='check-out-box']");

    private final SelenideElement datePickerWindow = $("[data-selenium='rangePickerCheckIn']");
    private final SelenideElement datePickerCaption = $("div.DayPicker-Caption");
    private final SelenideElement prevMonthButton = $("[aria-label='Previous month']");
    private final SelenideElement nextMonthButton = $("[aria-label='Next month']");

    private final SelenideElement occupancyToggle = $("[data-element-name='occupancy-box']");
    private final SelenideElement searchButton = $("[data-selenium='searchButton']");

    // Dynamic Locators
    private final String selectableDate = "[data-selenium-date='%s']";
    private final String searchSuggestion = "[data-text='%s']";
    private final String occupancyDisplayValue = "//div[div/h2[contains(text(), '%s')]]//p[contains(@class,'gpxKdd')]";
    private final String occupancyMinusButton = "//div[div/h2[contains(text(), '%s')]]//button[@data-selenium='minus']";
    private final String occupancyPlusButton = "//div[div/h2[contains(text(), '%s')]]//button[@data-selenium='plus']";


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

    private YearMonth getCurrentMonth() {
        String text = $("[data-selenium='calendar-month-year']").shouldBe(Condition.visible).getText().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return YearMonth.parse(text, formatter);
    }

    private void setTravelDate(LocalDate checkIn, LocalDate checkOut) {
//        if(!datePickerWindow.isDisplayed()){
//            checkInToggle.click();
//        }
        datePickerWindow.shouldBe(Condition.visible, Duration.ofSeconds(10));
        selectTravelDate(checkIn);
        selectTravelDate(checkOut);
    }

    private void selectTravelDate(LocalDate date) {
        DatePickerHelper datePickerHelper = new DatePickerHelper(
                $(datePickerCaption),
                $(nextMonthButton),
                $(prevMonthButton),
                $(selectableDate.formatted(date))
        );
        datePickerHelper.selectDate(date);
    }

    private void setOccupancy(int rooms, int adults, int children) {
        int currentRooms = Integer.parseInt($x(String.format(occupancyDisplayValue, LanguageManager.get("room"))).getText().trim());
        int currentAdults = Integer.parseInt($x(String.format(occupancyDisplayValue, LanguageManager.get("adults"))).getText().trim());
        int currentChildren = Integer.parseInt($x(String.format(occupancyDisplayValue, LanguageManager.get("children"))).getText().trim());

        adjustOccupancyValue(LanguageManager.get("room"), currentRooms, rooms);
        adjustOccupancyValue(LanguageManager.get("adults"), currentAdults, adults);
        adjustOccupancyValue(LanguageManager.get("children"), currentChildren, children);
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

    /**
     * Searches for a hotel with the specified parameters.
     *
     * @param place         The destination place to search for hotels.
     * @param checkInDate   The check-in date.
     * @param checkOutDate  The check-out date.
     * @param rooms         The number of rooms.
     * @param adults        The number of adults.
     * @param children      The number of children.
     */
    public void searchHotel(String place, LocalDate checkInDate, LocalDate checkOutDate, int rooms, int adults, int children) {
        changeLanguageAndCurrencyToVND();

        logHelper.logStep("Setting destination: %s", place);
        setDestination(place);

        logHelper.logStep("Setting check-in date: %s, check-out date: %s", checkInDate, checkOutDate);
        setTravelDate(checkInDate, checkOutDate);

        logHelper.logStep("Setting occupancy: Rooms: %d, Adults: %d, Children: %d", rooms, adults, children);
        setOccupancy(rooms, adults, children);

        logHelper.logStep("Clicking search button");
        searchButton.click();
    }

}
