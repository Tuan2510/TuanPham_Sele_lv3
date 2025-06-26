package pageObjects.VJPageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import testDataObject.VJTest.FlightDataObject;
import testDataObject.VJTest.FlightInfo;
import testDataObject.VJTest.TicketDataHolder;
import utils.ElementHelper;

import java.time.LocalDate;
import java.time.format.TextStyle;
import java.util.Locale;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.ElementHelper.clickWhenReady;
import static utils.ElementHelper.scrollToElement;
import static utils.ElementHelper.scrollToPageBottom;
import static utils.ElementHelper.scrollToPageTop;
import static utils.ElementHelper.switchToDefault;
import static utils.ElementHelper.switchToIframe;
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

    //Variable
    private final String flightCardAdditionalXpath = "/parent::div/parent::div/parent::div/preceding-sibling::div";

    //methods
    @Step("Close offer alert if displayed")
    public void closeOfferAlert() {
        if (alertOfferIframe.isDisplayed()) {
            switchToIframe(alertOfferIframe);

            if (alertOfferLaterBtn.isDisplayed()) {
                alertOfferLaterBtn.click();
            }

            switchToDefault();
        }
    }

    public void closeAdPanelButton(){
        clickWhenReady(closeAdPanelButton);
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

    public SelenideElement findCheapestTicket(){
        SelenideElement lowest = availableTicketCollection.get(0);
        int lowestPrice = parsePrice(availableTicketCollection.get(0).getText().trim());
        for (int i = 1; i < availableTicketCollection.size(); i++) {
            scrollToElement(availableTicketCollection.get(i));
            int price = parsePrice(availableTicketCollection.get(i).getText().trim());
            if(price < lowestPrice) {
                lowest = availableTicketCollection.get(i);
                lowestPrice = price;
            }
        }

        return lowest;
    }


    public void selectCheapestTicket(SelenideElement element){
        element.click();
    }

    public void verifyFlightDate(String expectedDate){
        selectingDate.getText().equalsIgnoreCase(expectedDate);
    }

    public FlightInfo extractFlightInfo(SelenideElement ticketElement){
        SelenideElement temp = ticketElement.$x("/div//span[contains(text(), 'VJ')]/ancestor::div[1]");
        System.out.println(temp.getText());

        String flightId = ticketElement.$x("/div//span[contains(text(), 'VJ')]/ancestor::div[1]").getText();
        String time  = ticketElement.$x("/div//span[contains(text(), 'To')]/ancestor::div[1]").getText();
        String plane  = ticketElement.$x("/div//span[contains(text(), '-')]/parent::span").getText();

        return new FlightInfo(flightId, time, plane);
    }

    public void verifyFlightInfo(FlightDataObject data){
        verifyTravelOptionPageDisplayed();
        TicketDataHolder ticketDataHolder = new TicketDataHolder();

        //verify currency
        verifyCurrencyIsVND();

        //verify flight depart and return address
        String expectedDepartAddress = data.getDepartmentLocation() + data.getDepartmentLocationCode();
        String expectedDestinationAddress = data.getDestinationLocation() + data.getDestinationLocationCode();
        verifyFlightLocation(expectedDepartAddress, expectedDestinationAddress);

        //verify flight type and passenger
        String expectedFlightTypeAndPassenger = data.getFlightTypeCode() + " | " + data.getFlightPassengerDataObject().getStringFlightPassenger();
        verifyFlightTypeAndPassenger(expectedFlightTypeAndPassenger);

        //verify depart date
        LocalDate expectedDepartLocalDate = LocalDate.now().plusDays(data.getDepartAfterDays());
        String expectedDepartDate = expectedDepartLocalDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " +
                expectedDepartLocalDate.getDayOfMonth() + getDayOfMonthSuffix(expectedDepartLocalDate.getDayOfMonth());
        verifyFlightDate(expectedDepartDate);

        //select lowest ticket
        SelenideElement lowestDepart = findCheapestTicket();
        SelenideElement departFlightCard = lowestDepart.$x(flightCardAdditionalXpath);
        ticketDataHolder.setDepartFlight(extractFlightInfo(departFlightCard));
        selectCheapestTicket(lowestDepart);

        continueButton.click();

        //verify return date
        LocalDate expectedReturnLocalDate = expectedDepartLocalDate.plusDays(data.getReturnAfterDays());
        String expectedReturnDate = expectedReturnLocalDate.getMonth().getDisplayName(TextStyle.FULL, Locale.ENGLISH) + " " +
                expectedReturnLocalDate.getDayOfMonth() + getDayOfMonthSuffix(expectedReturnLocalDate.getDayOfMonth());
        verifyFlightDate(expectedReturnDate);

        //select lowest ticket
        SelenideElement lowestReturn = findCheapestTicket();
        SelenideElement returnFlightCard = lowestReturn.$x(flightCardAdditionalXpath);
        ticketDataHolder.setReturnFlight(extractFlightInfo(returnFlightCard));
        selectCheapestTicket(lowestReturn);

        //continue to passenger info page
        continueButton.click();
    }

}
