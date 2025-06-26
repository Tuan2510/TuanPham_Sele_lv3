package pageObjects.VJPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import testDataObject.VJTest.FlightDataObject;
import testDataObject.VJTest.FlightPassengerDataObject;
import utils.ElementHelper;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.NumberHelper.getDayOfMonthSuffix;
import static utils.NumberHelper.parsePrice;

public class VJSelectTicketPage {
    //locators
    private final SelenideElement alertOfferIframe = $x("//iframe[@id='preview-notification-frame']");
    private final SelenideElement alertOfferLaterBtn = $("#NC_CTA_TWO");
    private final SelenideElement closeAdPanelButton = $x("//button[contains(@class, 'MuiButtonBase-root') and @aria-label='close']");
    private final ElementsCollection flightInfoCollection = $$x("//div[contains(@class, 'MuiBox-root')]//p[contains(@class, 'MuiTypography-root') and @variantmd='h3']");
    private final ElementsCollection soldOutTicketCollection = $$x("//div[contains(@class, 'MuiBox-root')]//p[contains(text(), 'Sold out')]");
    private final ElementsCollection availableTicketCollection = $$x("//div[contains(@class, 'MuiBox-root')]//p[contains(text(), '000 VND')]/preceding-sibling::p");

    private final SelenideElement selectingDate = $x("//div[contains(@class, 'lick-current')]//p[@weight='Bold']");
    private final SelenideElement lowestTicket = $x("");
    private final SelenideElement continueButton = $x("//button[contains(@class, 'MuiButtonBase-root MuiButton-root MuiButton-contained')]");


    //methods
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

    public void closeAdPanelButton(){
        if(closeAdPanelButton.isDisplayed() ){
            ElementHelper.clickWhenReady(closeAdPanelButton);
        }
    }

    public void verifyTravelOptionPageDisplayed(){
        webdriver().shouldHave(urlContaining("/select-flight"));
    }

    public void verifyCurrencyIsVND() {
        // Locate any element that contains the text "VND"
        $$("p.MuiTypography-root").findBy(text("VND")).shouldBe(visible);
        availableTicketCollection.shouldHave(sizeGreaterThan(0));
    }

    public void verifyFlightLocation(String expectedDepartCity, String expectedDestinationCity){
        String departureCity = null;
        String destinationCity = null;

        for (int i = 0; i < flightInfoCollection.size(); i++) {
            String text = flightInfoCollection.get(i).getText().trim();

            // Check for "From" and get next span as departure city
            if (text.toLowerCase().contains("from")) {
                departureCity = text.trim();
            }

            // Check for "To" and get next span as destination city
            if (text.toLowerCase().contains("to")) {
                destinationCity = text.trim();
            }
        }

        if (!departureCity.contains(expectedDepartCity)) {
            throw new AssertionError("Departure city mismatch: Expected " + expectedDepartCity + " but got " + departureCity);
        }

        if (!destinationCity.contains(expectedDestinationCity)) {
            throw new AssertionError("Destination city mismatch: Expected " + expectedDestinationCity + " but got " + destinationCity);
        }
    }

    public void verifyFlightTypeAndPassenger(String expectedFlightTypeAndPassenger){
        String flightTypeAndPassenger = null;

        for (int i = 0; i < flightInfoCollection.size(); i++) {
            String text = flightInfoCollection.get(i).getText().trim();
            // Check for flight type
            if (text.toLowerCase().contains("return") || text.toLowerCase().contains("one-way")) {
                flightTypeAndPassenger = text;
            }
        }

        if (!flightTypeAndPassenger.contains(expectedFlightTypeAndPassenger)) {
            throw new AssertionError("Flight type mismatch: Expected " + expectedFlightTypeAndPassenger + " but got " + flightTypeAndPassenger);
        }
    }

    public void selectCheapestTicket(){
        SelenideElement lowest = availableTicketCollection.get(0);
        int lowestPrice = parsePrice(availableTicketCollection.get(0).getText().trim());
        for (int i = 1; i < availableTicketCollection.size(); i++) {
            int price = parsePrice(availableTicketCollection.get(i).getText().trim());
            if(price < lowestPrice) {
                lowest = availableTicketCollection.get(i);
                lowestPrice = price;
            }
        }


        lowest.click();
    }

    public void verifyFlightDate(String expectedDate){
        selectingDate.getText().equalsIgnoreCase(expectedDate);
    }

    public void verifyFlightInfo(FlightDataObject data){
        verifyTravelOptionPageDisplayed();

        verifyCurrencyIsVND();

        String expectedDepartAddress = data.getDepartmentLocation() + data.getDepartmentLocationCode();
        String expectedDestinationAddress = data.getDestinationLocation() + data.getDestinationLocationCode();
        verifyFlightLocation(expectedDepartAddress, expectedDestinationAddress);

        String expectedFlightTypeAndPassenger = data.getFlightTypeCode() + " | " + data.getFlightPassengerDataObject().getStringFlightPassenger();
        verifyFlightTypeAndPassenger(expectedFlightTypeAndPassenger);

        LocalDate expectedDepartLocalDate = LocalDate.now().plusDays(data.getDepartAfterDays());
        String expectedDepartDate = expectedDepartLocalDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " +
                expectedDepartLocalDate.getDayOfMonth() + getDayOfMonthSuffix(expectedDepartLocalDate.getDayOfMonth());
        verifyFlightDate(expectedDepartDate);

        selectCheapestTicket();

        continueButton.click();

        LocalDate expectedReturnLocalDate = expectedDepartLocalDate.plusDays(data.getReturnAfterDays());
        String expectedReturnDate = expectedReturnLocalDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " +
                expectedReturnLocalDate.getDayOfMonth() + getDayOfMonthSuffix(expectedReturnLocalDate.getDayOfMonth());
        verifyFlightDate(expectedReturnDate);

        selectCheapestTicket();

        continueButton.click();
    }

}
