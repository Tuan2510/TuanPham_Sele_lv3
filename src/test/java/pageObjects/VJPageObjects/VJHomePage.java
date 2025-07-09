package pageObjects.VJPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import utils.ElementHelper;
import utils.LanguageManager;
import testDataObject.VJTest.FlightType;
import testDataObject.VJTest.FlightDataObject;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.TextStyle;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.DateHelper.getFormattedDate;
import static utils.ElementHelper.isElementDisplayed;
import static utils.LanguageManager.getLocale;

public class VJHomePage {
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
    @Step("Verify home page displayed in Vietnamese")
    public void verifyPageDisplayInVietnamese(){
        //check the url
        webdriver().shouldHave(urlContaining("/vi"));
    }

    @Step("Get flight input section for type: {type}")
    private SelenideElement getFlightInputSection(String dynamicValue) {
        return $x(TicketInput.formatted(dynamicValue));
    }

    @Step("Close cookies banner if displayed")
    public void closeCookiesBanner() {
        if (acceptCookiesBtn.isDisplayed()) {
            acceptCookiesBtn.click();
        }
    }

    @Step("Close offer alert if displayed")
    public void closeOfferAlert() {
        if (alertOfferIframe.isDisplayed()) {
            ElementHelper.switchToIframe(alertOfferIframe);

            if (alertOfferLaterBtn.isDisplayed()) {
                alertOfferLaterBtn.click();
            }

            ElementHelper.switchToDefault();
        }
    }

    @Step("Select flight type")
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

    @Step("Select departure location for flight")
    private void selectDepartureLocation(String DepartAddress){
        SelenideElement shadowLocationAddress = $x(shadowLocationXpath.formatted(DepartAddress));

        if (!shadowLocationAddress.isDisplayed()) {
            getFlightInputSection(LanguageManager.get("from")).click();
            getFlightInputSection(LanguageManager.get("from")).setValue(DepartAddress);
            shadowLocationAddress.should(appear);
        }

        shadowLocationAddress.click();
    }

    @Step("Select destination location for flight")
    private void selectDestinationLocation(String DesAddress){
        SelenideElement shadowLocationAddress = $x(shadowLocationXpath.formatted(DesAddress));

        if (!shadowLocationAddress.isDisplayed()) {
            $x(TicketInput.formatted(LanguageManager.get("to"))).click();
            $x(TicketInput.formatted(LanguageManager.get("to"))).setValue(DesAddress);
            shadowLocationAddress.should(appear);
        }

        shadowLocationAddress.click();
    }

    @Step("Select departure date and return date for round trip flight")
    private void selectRoundTripDate(LocalDate departDate, LocalDate returnDate){
        SelenideElement departDateBtn = $x(shadowDateButtonXpath.formatted(
                getFormattedDate(departDate, getLocale(), LanguageManager.get("month_format")), departDate.getDayOfMonth()));


        SelenideElement returnDayBtn = $x(shadowDateButtonXpath.formatted(
                getFormattedDate(departDate, getLocale(), LanguageManager.get("month_format")), returnDate.getDayOfMonth()));

        if (!isElementDisplayed(currentMonthLabel)) {
            departureDateBtn.click();
        }

        navigateToTargetMonth(departDate);
        departDateBtn.shouldBe(Condition.visible).click();

        navigateToTargetMonth(returnDate);
        returnDayBtn.shouldBe(Condition.visible).click();
    }

    @Step("Navigate to target month in the calendar")
    private void navigateToTargetMonth(LocalDate targetDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy", getLocale());

        YearMonth target = YearMonth.from(targetDate);

        for (int i = 0; i < 24; i++) { // avoid infinite loop
            String displayedText = currentMonthLabel.getText().trim();
            YearMonth current = YearMonth.parse(displayedText, formatter);

            if (current.equals(target)) break;

            if (current.isBefore(target)) {
                nextButton.shouldBe(Condition.visible).click();
            } else {
                prevButton.shouldBe(Condition.visible).click();
            }

            currentMonthLabel.shouldHave(Condition.text(
                    targetDate.getMonth().getDisplayName(TextStyle.FULL, getLocale())
            ));
        }
    }

    @Step("Select number of passenger in a flight")
    private void selectPassengerNumber(int numOfAdults, int numOfChildrens, int numOfInfants){

        if ($x(TicketInput.formatted(LanguageManager.get("passenger"))).isDisplayed()) {
            $x(TicketInput.formatted(LanguageManager.get("passenger"))).click();
        }

        adjustPassengerNumber("adults", numOfAdults);
        adjustPassengerNumber("children", numOfChildrens);
        adjustPassengerNumber("infants", numOfInfants);
    }

    @Step("Adjust the number of passengers")
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

    private void selectCheapestFlightOption() {
        if (!lowestPriceChb.isSelected()) {
            lowestPriceChb.setSelected(true);
        }
    }

    /**
     * Clicks the button to search for tickets based on the current form values.
     */
    @Step("Find ticket button click")
    public void findTicket(){
        if(passengerLetsGoBtn.isDisplayed()) {
            passengerLetsGoBtn.click();
        } else{
            letsGoBtn.click();
        }
    }

    /**
     * Fill in the search form with provided flight data and trigger the search.
     * @param data flight information
     */
    @Step("Search for tickets")
    public void searchTicket(FlightDataObject data){
        chooseFlightType(data.getFlightType());
        selectDepartureLocation(data.getDepartmentLocation());
        selectDestinationLocation(data.getDestinationLocation());

        LocalDate departLocalDate = LocalDate.now().plusDays(data.getDepartAfterDays());
        LocalDate returnLocalDate = departLocalDate.plusDays(data.getReturnAfterDays());

        selectRoundTripDate(departLocalDate, returnLocalDate);

        selectPassengerNumber(
                data.getFlightPassengerDataObject().getAdults(),
                data.getFlightPassengerDataObject().getChildren(),
                data.getFlightPassengerDataObject().getInfants());
        findTicket();
    }

    public void searchCheapestTicket(FlightDataObject data) {
        chooseFlightType(data.getFlightType());
        selectDepartureLocation(data.getDepartmentLocation());
        selectDestinationLocation(data.getDestinationLocation());

        LocalDate departLocalDate = LocalDate.now().plusDays(data.getDepartAfterDays());
        LocalDate returnLocalDate = departLocalDate.plusDays(data.getReturnAfterDays());

        selectRoundTripDate(departLocalDate, returnLocalDate);

        selectPassengerNumber(
                data.getFlightPassengerDataObject().getAdults(),
                data.getFlightPassengerDataObject().getChildren(),
                data.getFlightPassengerDataObject().getInfants());
        selectCheapestFlightOption();
        findTicket();
    }
}
