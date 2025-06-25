package pageObjects.VJPageObjects;

import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class VJHomePage {
    // Locators
    private final SelenideElement acceptCookiesBtn = $x("//div[@id='popup-dialog-description']/following-sibling::div/button']");
    private final SelenideElement alertOfferLaterBtn = $("#NC_CTA_TWO");
    private final SelenideElement roundTripRdb = $x("//span[@class='MuiIconButton-label']/input[@type='radio' and @value='roundTrip']");
    private final SelenideElement onewayRdb = $x("//span[@class='MuiIconButton-label']/input[@type='radio' and @value='oneway']");

    private final SelenideElement departmentInput = $x("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), 'From')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input");
    private final SelenideElement destinationInput = $x("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), 'To')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input");
    private final SelenideElement passengerInput = $x("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), 'Passenger')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input");
    private final SelenideElement departureDateBtn = $x("//div[@role='button']//p[contains(text(), 'Departure date')]");
    private final SelenideElement lowestPriceChb = $x("//span[@class='MuiIconButton-label']/input[@type='checkbox' and @value='secondary']");
    private final SelenideElement letsGoBtn = $x("//button[@class='MuiButtonBase-root MuiButton-root MuiButton-contained']");

    // Dynamic Locators
    private final String shadowLocationXpath = "//div[contains(@class, 'MuiBox-root')]//div[contains(text(), '%s')]";
    private final String shadowDateButtonXpath = "//div[@class='rdrMonthName' and contains(text(), '%s')]/parent::div//span[contains(text(), '%s')]";
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
        if (alertOfferLaterBtn.isDisplayed()) {
            alertOfferLaterBtn.click();
        }
    }

    @Step("Select flight type")
    public void chooseFlightType(String type){
        if (type.equalsIgnoreCase("roundTrip")){
            roundTripRdb.click();
        } else if (type.equalsIgnoreCase("oneway")){
            onewayRdb.click();
        }
    }

    @Step("Select departure location for flight")
    public void selectDepartureLocation(String DepartAddress){
        SelenideElement shadowLocationHCM = $x(shadowLocationXpath.formatted(DepartAddress));

        if (!shadowLocationHCM.isDisplayed()) {
            departmentInput.click();
            shadowLocationHCM.should(appear);
        }

        shadowLocationHCM.click();
    }

    @Step("Select destination location for flight")
    public void selectDestinationLocation(String DesAddress){
        SelenideElement shadowLocationHN = $x(shadowLocationXpath.formatted(DesAddress));

        if (!shadowLocationHN.isDisplayed()) {
            destinationInput.click();
            shadowLocationHN.should(appear);
        }

        shadowLocationHN.click();
    }

    @Step("Select departure date and return date for round trip flight")
    public void selectRoundTripDate(int DepartDays, int ReturnDays){
        LocalDate departureDate = LocalDate.now().plusDays(DepartDays);
        LocalDate returnDate = departureDate.plusDays(ReturnDays);


        SelenideElement departDateBtn = $x(shadowDateButtonXpath.formatted(
                departureDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), departureDate.getDayOfMonth()));

        SelenideElement returnDayBtn = $x(shadowDateButtonXpath.formatted(
                returnDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), returnDate.getDayOfMonth()));

        if (!departDateBtn.isDisplayed()) {
            departureDateBtn.click();
        }

        departDateBtn.click();
        returnDayBtn.click();
    }

    @Step("Select number of passenger in a flight")
    public void selectPassengerNumber(int numOfAdults, int numOfChildrens, int numOfInfants){
        selectAdultPassengerNumber(numOfAdults);
        selectChildrenPassengerNumber(numOfChildrens);
        selectInfantsPassengerNumber(numOfInfants);
    }

    @Step("Select number of Adult(s) in a flight")
    public void selectAdultPassengerNumber(int numOfAdults){
        SelenideElement adultsPassengerMinusBtn = $x(shadowPassengerMinusBtn.formatted("Adults"));
        SelenideElement adultsPassengerNumber = $x(shadowPassengerNumber.formatted("Adults"));
        SelenideElement adultsPassengerPlusBtn = $x(shadowPassengerPlusBtn.formatted("Adults"));

        if (!adultsPassengerMinusBtn.isDisplayed()) {
            passengerInput.click();
        }

        while (Integer.parseInt(adultsPassengerNumber.getText()) < numOfAdults){
            adultsPassengerPlusBtn.click();
        }

        while (Integer.parseInt(adultsPassengerNumber.getText()) > numOfAdults){
            adultsPassengerMinusBtn.click();
        }

    }

    @Step("Select number of Children(s) in a flight")
    public void selectChildrenPassengerNumber(int numOfChildrens){
        SelenideElement childrensPassengerMinusBtn = $x(shadowPassengerMinusBtn.formatted("Childrens"));
        SelenideElement childrensPassengerNumber = $x(shadowPassengerNumber.formatted("Childrens"));
        SelenideElement childrensPassengerPlusBtn = $x(shadowPassengerPlusBtn.formatted("Childrens"));

        if (!childrensPassengerMinusBtn.isDisplayed()) {
            passengerInput.click();
        }

        while (Integer.parseInt(childrensPassengerNumber.getText()) < numOfChildrens){
            childrensPassengerPlusBtn.click();
        }

        while (Integer.parseInt(childrensPassengerNumber.getText()) > numOfChildrens){
            childrensPassengerMinusBtn.click();
        }
    }

    @Step("Select number of Infant(s) in a flight")
    public void selectInfantsPassengerNumber(int numOfInfants){
        SelenideElement infantsPassengerMinusBtn = $x(shadowPassengerMinusBtn.formatted("Infants"));
        SelenideElement infantsPassengerNumber = $x(shadowPassengerNumber.formatted("Infants"));
        SelenideElement infantsPassengerPlusBtn = $x(shadowPassengerPlusBtn.formatted("Infants"));

        if (!infantsPassengerMinusBtn.isDisplayed()) {
            passengerInput.click();
        }

        while (Integer.parseInt(infantsPassengerNumber.getText()) < numOfInfants){
            infantsPassengerPlusBtn.click();
        }

        while (Integer.parseInt(infantsPassengerNumber.getText()) > numOfInfants){
            infantsPassengerMinusBtn.click();
        }
    }

    public void findTicket(){
        letsGoBtn.click();
    }

}
