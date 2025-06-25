package pageObjects.VJPageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import testDataObject.VJTest.FlightPassengerDataObject;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$$x;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.WebDriverRunner.url;
import static org.testng.Assert.assertTrue;
import static utils.NumberHelper.getDayOfMonthSuffix;

public class VJSelectTicketPage {
    //locators
    private final SelenideElement closeAdPanelButton = $x("//button[contains(@class, 'MuiButtonBase-root') and @aria-label='close']");
    private final ElementsCollection flightInfoCollection = $$x("//div[contains(@class, 'MuiBox-root')]//p[contains(@class, 'MuiTypography-root') and @variantmd='h3']");
    private final ElementsCollection soldOutTicketCollection = $$x("//div[contains(@class, 'MuiBox-root')]//p[contains(text(), 'Sold out')]");
    private final ElementsCollection availableTicketCollection = $$x("//div[contains(@class, 'MuiBox-root')]//p[contains(text(), '000 VND')]");

    private final SelenideElement selectingDate = $x("//div[contains(@class, 'lick-current')]//p[@weight='Bold']");
    private final SelenideElement lowestTicket = $x("");
    private final SelenideElement continueButton = $x("//button[contains(@class, 'MuiButtonBase-root MuiButton-root MuiButton-contained')]");


    //methods
    public void closeAdPanelButton(){
        if(closeAdPanelButton.isDisplayed()){
            closeAdPanelButton.click();
        }
    }

    public void verifyTravelOptionPageDisplayed(){
        assertTrue(url().contains("/select-flight"), "Select Travel Options page is not displayed.");
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
            if (text.equalsIgnoreCase("From") && (i + 1) < flightInfoCollection.size()) {
                departureCity = flightInfoCollection.get(i + 1).getText().trim();
            }

            // Check for "To" and get next span as destination city
            if (text.equalsIgnoreCase("To") && (i + 1) < flightInfoCollection.size()) {
                destinationCity = flightInfoCollection.get(i + 1).getText().trim();
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

        if (!expectedFlightTypeAndPassenger.equalsIgnoreCase(flightTypeAndPassenger)) {
            throw new AssertionError("Flight type mismatch: Expected " + expectedFlightTypeAndPassenger + " but got " + flightTypeAndPassenger);
        }
    }

    public void verifyFlightDate(String expectedDate){
        selectingDate.getText().equalsIgnoreCase(expectedDate);
    }

    public void verifyFlightInfo(String expectedFlightType, FlightPassengerDataObject expectedPassenger, String expectedDepartCity, String expectedDestinationCity, int departAfterDays){
        verifyTravelOptionPageDisplayed();

        verifyCurrencyIsVND();

        verifyFlightLocation(expectedDepartCity, expectedDestinationCity);

        String expectedFlightTypeAndPassenger = expectedFlightType + " | " + expectedPassenger.getStringFlightPassenger();
        verifyFlightTypeAndPassenger(expectedFlightTypeAndPassenger);

        LocalDate expectedLocalDate = LocalDate.now().plusDays(departAfterDays);
        String expectedDate = expectedLocalDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " +
                expectedLocalDate.getDayOfMonth() + getDayOfMonthSuffix(expectedLocalDate.getDayOfMonth());
        verifyFlightDate(expectedDate);

    }

}
