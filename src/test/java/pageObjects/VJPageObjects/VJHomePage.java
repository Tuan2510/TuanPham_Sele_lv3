package pageObjects.VJPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import utils.ElementHelper;
import utils.LanguageManager;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;

public class VJHomePage {
    // Locators
    private final SelenideElement acceptCookiesBtn = $("#popup-dialog-description + div button");
    private final SelenideElement alertOfferIframe = $("#preview-notification-frame");
    private final SelenideElement alertOfferLaterBtn = $("#NC_CTA_TWO");
    private final SelenideElement roundTripRdb = $("span.MuiIconButton-label input[type='radio'][value='roundTrip']");
    private final SelenideElement onewayRdb = $("span.MuiIconButton-label input[type='radio'][value='oneway']");

    private final SelenideElement departmentInput = $x("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), '" + LanguageManager.get("from") + "')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input");
    private final SelenideElement destinationInput = $x("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), '" + LanguageManager.get("to") + "')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input");
    private final SelenideElement passengerInput = $x("//label[contains(@class, 'MuiFormLabel-root') and contains(text(), '" + LanguageManager.get("passenger") + "')]/following-sibling::div[contains(@class, 'MuiInputBase-root')]/input");
    private final SelenideElement departureDateBtn = $x("//p[contains(text(), '" + LanguageManager.get("departure_date") + "')]/parent::div//ancestor::div[@role='button']");
    private final SelenideElement lowestPriceChb = $("span.MuiIconButton-label input[type='checkbox'][value='secondary']");
    private final SelenideElement letsGoBtn = $("button.MuiButtonBase-root.MuiButton-root.MuiButton-contained");
    private final SelenideElement passengerLetsGoBtn = $x("//div[contains(@class,'MuiBox-root')]/following-sibling::div//span[contains(@class, 'MuiTypography-root') and text()=\"" + LanguageManager.get("lets_go") + "\"]/ancestor::button");

    // Dynamic Locators
    private final String shadowLocationXpath = "//div[contains(@class, 'MuiBox-root')]//div[contains(text(), '%s')]";
    private final String shadowDateButtonXpath = "//div[@class='rdrMonthName' and contains(text(), '%s')]/parent::div//span[text()= '%s']/ancestor::button[contains(@class, 'rdrDay') and not(contains(@class, 'rdrDayDisabled'))]";
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
    public void chooseFlightType(String type){
        if (type.equalsIgnoreCase("roundTrip")){
            roundTripRdb.click();
        } else if (type.equalsIgnoreCase("oneway")){
            onewayRdb.click();
        }
    }

    @Step("Select departure location for flight")
    public void selectDepartureLocation(String DepartAddress){
        SelenideElement shadowLocationAddress = $x(shadowLocationXpath.formatted(DepartAddress));

        if (!shadowLocationAddress.isDisplayed()) {
            departmentInput.click();
            departmentInput.setValue("Ho Chi Minh");
            shadowLocationAddress.should(appear);
        }

        shadowLocationAddress.click();
    }

    @Step("Select destination location for flight")
    public void selectDestinationLocation(String DesAddress){
        SelenideElement shadowLocationAddress = $x(shadowLocationXpath.formatted(DesAddress));

        if (!shadowLocationAddress.isDisplayed()) {
            destinationInput.click();
            destinationInput.setValue("Ha Noi");
            shadowLocationAddress.should(appear);
        }

        shadowLocationAddress.click();
    }

    @Step("Select departure date and return date for round trip flight")
    public void selectRoundTripDate(int departAfterDays, int returnAfterDays){
        LocalDate departDate = LocalDate.now().plusDays(departAfterDays);
        LocalDate returnDate = departDate.plusDays(returnAfterDays);


        SelenideElement departDateBtn = $x(shadowDateButtonXpath.formatted(
                departDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), departDate.getDayOfMonth()));

        SelenideElement returnDayBtn = $x(shadowDateButtonXpath.formatted(
                returnDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH), returnDate.getDayOfMonth()));

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

        selectAdultPassengerNumber(numOfAdults);
        selectChildrenPassengerNumber(numOfChildrens);
        selectInfantsPassengerNumber(numOfInfants);
    }

    @Step("Select number of Adult(s) in a flight")
    public void selectAdultPassengerNumber(int numOfAdults){
        SelenideElement adultsPassengerMinusBtn = $x(shadowPassengerMinusBtn.formatted(LanguageManager.get("adults")));
        SelenideElement adultsPassengerNumber = $x(shadowPassengerNumber.formatted(LanguageManager.get("adults")));
        SelenideElement adultsPassengerPlusBtn = $x(shadowPassengerPlusBtn.formatted(LanguageManager.get("adults")));

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
        SelenideElement childrensPassengerMinusBtn = $x(shadowPassengerMinusBtn.formatted(LanguageManager.get("children")));
        SelenideElement childrensPassengerNumber = $x(shadowPassengerNumber.formatted(LanguageManager.get("children")));
        SelenideElement childrensPassengerPlusBtn = $x(shadowPassengerPlusBtn.formatted(LanguageManager.get("children")));

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
        SelenideElement infantsPassengerMinusBtn = $x(shadowPassengerMinusBtn.formatted(LanguageManager.get("infants")));
        SelenideElement infantsPassengerNumber = $x(shadowPassengerNumber.formatted(LanguageManager.get("infants")));
        SelenideElement infantsPassengerPlusBtn = $x(shadowPassengerPlusBtn.formatted(LanguageManager.get("infants")));

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
        if(passengerLetsGoBtn.isDisplayed()) {
            passengerLetsGoBtn.click();
        } else{
            letsGoBtn.click();
        }
    }

}
