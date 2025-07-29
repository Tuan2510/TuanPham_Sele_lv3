package pageObjects.VJPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ElementHelper;
import utils.LanguageManager;
import testDataObject.VJTest.FlightType;
import testDataObject.VJTest.FlightDataObject;
import utils.LogHelper;
import utils.TestListener;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.DateHelper.getFormattedDate;
import static utils.ElementHelper.isElementDisplayed;
import static utils.LanguageManager.getLocale;

public class VJHomePage {
    private static final Logger logger = LoggerFactory.getLogger(VJHomePage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    // Locators
    private final SelenideElement acceptCookiesBtn = $("#popup-dialog-description + div button");
    private final SelenideElement alertOfferIframe = $("#preview-notification-frame");
    private final SelenideElement alertOfferLaterBtn = $("#NC_CTA_TWO");
    private final SelenideElement roundTripRdb = $("span.MuiIconButton-label input[type='radio'][value='roundTrip']");
    private final SelenideElement onewayRdb = $("span.MuiIconButton-label input[type='radio'][value='oneway']");

    SelenideElement currentMonthLabel = $$x("//div[contains(@class, 'rdrMonthName')]").first();
    private final SelenideElement nextButton = $x("//button[contains(@class, 'rdrNextButton')]");
    private final SelenideElement prevButton = $x("//button[contains(@class, 'rdrPprevButton')]");

    private final SelenideElement departureDateBtn = $x(String.format("//div[@role='button'][.//p[contains(text(), '%s')]]", LanguageManager.get("departure_date")));
    private final SelenideElement lowestPriceChb = $("span.MuiIconButton-label input[type='checkbox'][value='secondary']");
    private final SelenideElement passengerLowestPriceChb = $("span.MuiIconButton-label input[type='checkbox'][value='primary']");
    private final SelenideElement letsGoBtn = $("button.MuiButtonBase-root.MuiButton-root.MuiButton-contained");
    private final SelenideElement passengerLetsGoBtn = $x(String.format("//button[span/span[text()=\"%s\"]]", LanguageManager.get("lets_go")));

    // Dynamic Locators
    private final String TicketInput = "//div[./label[contains(@class, 'MuiFormLabel-root')][contains(text(), '%s')]]//input";
    private final String shadowLocationXpath = "//div[contains(@class, 'MuiBox-root')]//div[contains(text(), '%s')]";
    private final String shadowDateButtonXpath = "//div[./div[contains(text(), '%s')]]//button[.//span[text()='%s']][not(contains(@class, 'rdrDayDisabled'))]";
    private final String shadowPassengerMinusBtn = "//div[div/div/p[text()='%s']]//button[1]";
    private final String shadowPassengerNumber = "//div[div/div/p[text()='%s']]/div/span";
    private final String shadowPassengerPlusBtn = "//div[div/div/p[text()='%s']]//button[2]";

    // Methods
    /**
     * Verifies that the home page is displayed by checking the URL.
     * This method uses Selenide's webdriver to assert that the current URL contains "/home".
     */
    public void verifyPageDisplayInVietnamese(){
        //check the url
        webdriver().shouldHave(urlContaining("/vi"));
    }

    private SelenideElement getFlightInputSection(String dynamicValue) {
        return $x(TicketInput.formatted(dynamicValue));
    }

    /**
     * Closes the cookies banner if it is displayed.
     * This method checks for the presence of the accept cookies button and clicks it if visible.
     */
    public void closeCookiesBanner() {
        if (acceptCookiesBtn.isDisplayed()) {
            acceptCookiesBtn.click();
        }
    }

    /**
     * Closes the offer alert if it is displayed.
     * This method switches to the iframe containing the alert and clicks the "Later" button if it exists.
     */
    public void closeOfferAlert() {
        if (alertOfferIframe.isDisplayed()) {
            ElementHelper.switchToIframe(alertOfferIframe);

            if (alertOfferLaterBtn.isDisplayed()) {
                alertOfferLaterBtn.click();
            }

            ElementHelper.switchToDefault();
        }
    }

    private void chooseFlightType(FlightType type){
        if (type == FlightType.ROUND_TRIP){
            if(!roundTripRdb.isSelected()) {
                roundTripRdb.setSelected(true);
            }
        } else if (type == FlightType.ONE_WAY){
            if(!onewayRdb.isSelected()) {
                onewayRdb.setSelected(true);
            }
        }
    }

    private void selectDepartureLocation(String DepartAddress){
        SelenideElement shadowLocationAddress = $x(shadowLocationXpath.formatted(DepartAddress));

        if (!shadowLocationAddress.isDisplayed()) {
            getFlightInputSection(LanguageManager.get("from")).click();
            getFlightInputSection(LanguageManager.get("from")).setValue(DepartAddress);
            shadowLocationAddress.should(appear);
        }

        shadowLocationAddress.click();
    }

    private void selectDestinationLocation(String DesAddress){
        SelenideElement shadowLocationAddress = $x(shadowLocationXpath.formatted(DesAddress));

        if (!shadowLocationAddress.isDisplayed()) {
            $x(TicketInput.formatted(LanguageManager.get("to"))).click();
            $x(TicketInput.formatted(LanguageManager.get("to"))).setValue(DesAddress);
            shadowLocationAddress.should(appear);
        }

        shadowLocationAddress.click();
    }

    private void selectRoundTripDate(LocalDate departDate, LocalDate returnDate){
        SelenideElement departDateBtn = $x(shadowDateButtonXpath.formatted(
                getFormattedDate(departDate, getLocale(), LanguageManager.get("month_format")), departDate.getDayOfMonth()));


        SelenideElement returnDayBtn = $x(shadowDateButtonXpath.formatted(
                getFormattedDate(returnDate, getLocale(), LanguageManager.get("month_format")), returnDate.getDayOfMonth()));

        if (!isElementDisplayed(currentMonthLabel)) {
            departureDateBtn.click();
        }

        navigateToTargetMonth(departDate);
        departDateBtn.shouldBe(visible).click();

        navigateToTargetMonth(returnDate);
        returnDayBtn.shouldBe(visible).click();
    }

    private void navigateToTargetMonth(LocalDate targetDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(LanguageManager.get("month_year_format"), getLocale());

        YearMonth target = YearMonth.from(targetDate);

        for (int i = 0; i < 24; i++) { // avoid infinite loop
            String displayedText = currentMonthLabel.getText().replace("Tháng", "").trim();
            YearMonth current = YearMonth.parse(displayedText, formatter);

            if (current.equals(target)) break;

            if (current.isBefore(target)) {
                nextButton.shouldBe(visible).click();
            } else {
                prevButton.shouldBe(visible).click();
            }
        }

        if(getLocale().equals(Locale.of("vi", "vn"))) {
            String monthWithLeadingZero = String.format("Tháng %02d", targetDate.getMonthValue());
            currentMonthLabel.shouldHave(Condition.text(monthWithLeadingZero));
        } else {
            currentMonthLabel.shouldHave(Condition.text(
                    targetDate.getMonth().getDisplayName(TextStyle.FULL, getLocale())
            ));
        }
    }

    private void selectPassengerNumber(int numOfAdults, int numOfChildrens, int numOfInfants){

        if ($x(TicketInput.formatted(LanguageManager.get("passenger"))).isDisplayed()) {
            $x(TicketInput.formatted(LanguageManager.get("passenger"))).click();
        }

        adjustPassengerNumber("adults", numOfAdults);
        adjustPassengerNumber("children", numOfChildrens);
        adjustPassengerNumber("infants", numOfInfants);
    }

    private void adjustPassengerNumber(String passengerKey, int target){
        SelenideElement minusBtn = $x(shadowPassengerMinusBtn.formatted(LanguageManager.get(passengerKey)));
        SelenideElement currentNumber = $x(shadowPassengerNumber.formatted(LanguageManager.get(passengerKey)));
        SelenideElement plusBtn = $x(shadowPassengerPlusBtn.formatted(LanguageManager.get(passengerKey)));

        if (!minusBtn.isDisplayed()) {
            $x(TicketInput.formatted(LanguageManager.get("passenger"))).click();
        }

        while (Integer.parseInt(currentNumber.getText()) < target){
            plusBtn.click();
        }

        while (Integer.parseInt(currentNumber.getText()) > target){
            minusBtn.click();
        }
    }

    private void checkLowestPriceCheckbox() {
        if (passengerLowestPriceChb.is(Condition.clickable, Duration.ofSeconds(10))) {
            passengerLowestPriceChb.click();
        } else {
            lowestPriceChb.click();
        }
    }

    /**
     * Clicks the button to search for tickets based on the current form values.
     */
    public void findTicket(){
//        if(passengerLetsGoBtn.isDisplayed()) {
            passengerLetsGoBtn.click();
//        } else{
//            letsGoBtn.click();
//        }
    }

    /**
     * Fill in the search form with provided flight data and trigger the search.
     * @param data flight information
     */
    public void searchTicket(FlightDataObject data){
        logHelper.logStep("Searching for tickets with flight type: %s", data.getFlightType());
        chooseFlightType(data.getFlightType());

        logHelper.logStep("Selecting departure location: %s", data.getDepartmentLocation());
        selectDepartureLocation(data.getDepartmentLocation());

        logHelper.logStep("Selecting destination location: %s", data.getDestinationLocation());
        selectDestinationLocation(data.getDestinationLocation());

        LocalDate departLocalDate = LocalDate.now().plusDays(data.getDepartAfterDays());
        LocalDate returnLocalDate = departLocalDate.plusDays(data.getReturnAfterDays());
        logHelper.logStep("Selecting round trip date: Depart - %s, Return - %s",
                departLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                returnLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        selectRoundTripDate(departLocalDate, returnLocalDate);

        logHelper.logStep("Selecting passenger number: Adults - %s, Children - %s, Infants - %s",
                data.getFlightPassengerDataObject().getAdults(),
                data.getFlightPassengerDataObject().getChildren(),
                data.getFlightPassengerDataObject().getInfants());
        selectPassengerNumber(
                data.getFlightPassengerDataObject().getAdults(),
                data.getFlightPassengerDataObject().getChildren(),
                data.getFlightPassengerDataObject().getInfants());

        logHelper.logStep("Clicking find ticket button");
        findTicket();
    }

    /**
     * Searches for tickets with the lowest fare option.
     * This method fills in the flight search form with the provided data and checks the lowest price checkbox.
     *
     * @param data The flight data object containing flight details.
     */
    public void searchTicketWithLowestOption(FlightDataObject data){
        logHelper.logStep("Searching for tickets with flight type: %s", data.getFlightType());
        chooseFlightType(data.getFlightType());

        logHelper.logStep("Selecting departure location: %s", data.getDepartmentLocation());
        selectDepartureLocation(data.getDepartmentLocation());

        logHelper.logStep("Selecting destination location: %s", data.getDestinationLocation());
        selectDestinationLocation(data.getDestinationLocation());

        LocalDate departLocalDate = LocalDate.now().plusDays(data.getDepartAfterDays());
        LocalDate returnLocalDate = departLocalDate.plusDays(data.getReturnAfterDays());

        logHelper.logStep("Selecting round trip date: Depart - %s, Return - %s",
                departLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                returnLocalDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
        selectRoundTripDate(departLocalDate, returnLocalDate);

        logHelper.logStep("Selecting passenger number: Adults - %s, Children - %s, Infants - %s",
                data.getFlightPassengerDataObject().getAdults(),
                data.getFlightPassengerDataObject().getChildren(),
                data.getFlightPassengerDataObject().getInfants());
        selectPassengerNumber(
                data.getFlightPassengerDataObject().getAdults(),
                data.getFlightPassengerDataObject().getChildren(),
                data.getFlightPassengerDataObject().getInfants());

        logHelper.logStep("Checking lowest price checkbox");
        checkLowestPriceCheckbox();

        logHelper.logStep("Clicking find ticket button");
        findTicket();
    }
}
