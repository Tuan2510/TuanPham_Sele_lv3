package pageObjects.VJPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import utils.ElementHelper;
import utils.LanguageManager;
import utils.DateHelper;
import testDataObject.VJTest.FlightType;
import testDataObject.VJTest.FlightDataObject;

import java.time.LocalDate;
import java.time.format.TextStyle;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static utils.LanguageManager.getLocale;

public class VJHomePage {
    // Locators
    private final SelenideElement acceptCookiesBtn = $("#popup-dialog-description + div button");
    private final SelenideElement alertOfferIframe = $("#preview-notification-frame");
    private final SelenideElement alertOfferLaterBtn = $("#NC_CTA_TWO");
    private final SelenideElement roundTripRdb = $("span.MuiIconButton-label input[type='radio'][value='roundTrip']");
    private final SelenideElement onewayRdb = $("span.MuiIconButton-label input[type='radio'][value='oneway']");

    private final SelenideElement departmentInput = $x(String.format("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), '%s')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input", LanguageManager.get("from")));
    private final SelenideElement destinationInput = $x(String.format("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), '%s')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input", LanguageManager.get("to")));
    private final SelenideElement passengerInput = $x(String.format("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), '%s')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input", LanguageManager.get("passenger")));
    private final SelenideElement departureDateBtn = $x(String.format("//p[contains(text(), '%s')]/parent::div//ancestor::div[@role='button']", LanguageManager.get("departure_date")));
    private final SelenideElement lowestPriceChb = $("span.MuiIconButton-label input[type='checkbox'][value='secondary']");
    private final SelenideElement letsGoBtn = $("button.MuiButtonBase-root.MuiButton-root.MuiButton-contained");
    private final SelenideElement passengerLetsGoBtn = $x(String.format("//span[contains(@class, 'MuiTypography-root') and text()=\"%s\"]/ancestor::button", LanguageManager.get("lets_go")));

    // Dynamic Locators
    private final String shadowLocationXpath = "//div[contains(@class, 'MuiBox-root')]//div[contains(text(), '%s')]";
    private final String shadowDateButtonXpath = "//div[contains(text(), '%s')]/parent::div//span[text()= '%s']/ancestor::button[not(contains(@class, 'rdrDayPassive'))]";
    private final String shadowPassengerMinusBtn = "//p[text()='%s']/ancestor::div[contains(@class,'MuiBox-root')][3]//button[1]";
    private final String shadowPassengerNumber = "//p[text()='%s']/ancestor::div[contains(@class,'MuiBox-root')][3]/div/span";
    private final String shadowPassengerPlusBtn = "//p[text()='%s']/ancestor::div[contains(@class,'MuiBox-root')][3]//button[2]";

    // Methods
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
    public void chooseFlightType(FlightType type){
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
    public void selectDepartureLocation(String DepartAddress){
        SelenideElement shadowLocationAddress = $x(shadowLocationXpath.formatted(DepartAddress));

        if (!shadowLocationAddress.isDisplayed()) {
            departmentInput.click();
            departmentInput.setValue(DepartAddress);
            shadowLocationAddress.should(appear);
        }

        shadowLocationAddress.click();
    }

    @Step("Select destination location for flight")
    public void selectDestinationLocation(String DesAddress){
        SelenideElement shadowLocationAddress = $x(shadowLocationXpath.formatted(DesAddress));

        if (!shadowLocationAddress.isDisplayed()) {
            destinationInput.click();
            destinationInput.setValue(DesAddress);
            shadowLocationAddress.should(appear);
        }

        shadowLocationAddress.click();
    }

    @Step("Select departure date and return date for round trip flight")
    public void selectRoundTripDate(int departAfterDays, int returnAfterDays){
        LocalDate departDate = LocalDate.now().plusDays(departAfterDays);
        LocalDate returnDate = departDate.plusDays(returnAfterDays);


        SelenideElement departDateBtn = $x(shadowDateButtonXpath.formatted(
                departDate.getMonth().getDisplayName(TextStyle.FULL, getLocale()), departDate.getDayOfMonth()));

        SelenideElement returnDayBtn = $x(shadowDateButtonXpath.formatted(
                returnDate.getMonth().getDisplayName(TextStyle.FULL, getLocale()), returnDate.getDayOfMonth()));

        if (departDateBtn.isDisplayed()) {
            departureDateBtn.click();
        }

        departDateBtn.shouldBe(Condition.visible).click();
        returnDayBtn.shouldBe(Condition.visible).click();
    }

    /**
     * Select departure and return dates using human friendly descriptors.
     * Examples of descriptors: "next monday", "next weekend", "next month".
     */
    @Step("Select departure date and return date for round trip flight (human friendly date descriptions)")
    public void selectRoundTripDate(String departDescriptor, String returnDescriptor){
        LocalDate departDate = DateHelper.parseFriendly(departDescriptor);
        LocalDate returnDate = DateHelper.parseFriendly(returnDescriptor);

        SelenideElement departDateBtn = $x(shadowDateButtonXpath.formatted(
                departDate.getMonth().getDisplayName(TextStyle.FULL, getLocale()), departDate.getDayOfMonth()));

        SelenideElement returnDayBtn = $x(shadowDateButtonXpath.formatted(
                returnDate.getMonth().getDisplayName(TextStyle.FULL, getLocale()), returnDate.getDayOfMonth()));

        if (departDateBtn.isDisplayed()) {
            departureDateBtn.click();
        }

        departDateBtn.shouldBe(Condition.visible).click();
        returnDayBtn.shouldBe(Condition.visible).click();
    }

    @Step("Select number of passenger in a flight")
    public void selectPassengerNumber(int numOfAdults, int numOfChildrens, int numOfInfants){

        if (passengerInput.isDisplayed()) {
            passengerInput.click();
        }

        adjustPassengerNumber("adults", numOfAdults);
        adjustPassengerNumber("children", numOfChildrens);
        adjustPassengerNumber("infants", numOfInfants);
    }

    @Step("Select number of Adult(s) in a flight")
    public void selectAdultPassengerNumber(int numOfAdults){
        adjustPassengerNumber("adults", numOfAdults);

    }

    @Step("Select number of Children(s) in a flight")
    public void selectChildrenPassengerNumber(int numOfChildrens){
        adjustPassengerNumber("children", numOfChildrens);
    }

    @Step("Select number of Infant(s) in a flight")
    public void selectInfantsPassengerNumber(int numOfInfants){
        adjustPassengerNumber("infants", numOfInfants);
    }

    private void adjustPassengerNumber(String passengerKey, int target){
        SelenideElement minusBtn = $x(shadowPassengerMinusBtn.formatted(LanguageManager.get(passengerKey)));
        SelenideElement currentNumber = $x(shadowPassengerNumber.formatted(LanguageManager.get(passengerKey)));
        SelenideElement plusBtn = $x(shadowPassengerPlusBtn.formatted(LanguageManager.get(passengerKey)));

        if (!minusBtn.isDisplayed()) {
            passengerInput.click();
        }

        while (Integer.parseInt(currentNumber.getText()) < target){
            plusBtn.click();
        }

        while (Integer.parseInt(currentNumber.getText()) > target){
            minusBtn.click();
        }
    }

    public void findTicket(){
        if(passengerLetsGoBtn.isDisplayed()) {
            passengerLetsGoBtn.click();
        } else{
            letsGoBtn.click();
        }
    }

    @Step("Search for tickets")
    public void searchTicket(FlightDataObject data){
        chooseFlightType(data.getFlightType());
        selectDepartureLocation(data.getDepartmentLocation());
        selectDestinationLocation(data.getDestinationLocation());
        selectRoundTripDate(data.getDepartAfterDays(), data.getReturnAfterDays());
        selectPassengerNumber(
                data.getFlightPassengerDataObject().getAdults(),
                data.getFlightPassengerDataObject().getChildren(),
                data.getFlightPassengerDataObject().getInfants());
        findTicket();
    }

}
