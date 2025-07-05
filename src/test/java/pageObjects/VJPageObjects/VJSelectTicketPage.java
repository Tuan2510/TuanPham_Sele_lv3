package pageObjects.VJPageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import testDataObject.VJTest.FlightDataObject;
import testDataObject.VJTest.FlightCardInfo;
import testDataObject.VJTest.FlightCardDataHolder;
import utils.LanguageManager;

import java.time.LocalDate;
import java.time.format.TextStyle;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.ElementHelper.clickWhenReady;
import static utils.ElementHelper.scrollToElement;
import static utils.ElementHelper.switchToDefault;
import static utils.ElementHelper.switchToIframe;
import static utils.NumberHelper.getNumberSuffix;
import static utils.NumberHelper.parsePrice;
import static utils.LanguageManager.getLocale;

public class VJSelectTicketPage {
    //Locators
    private final SelenideElement alertOfferIframe = $("#preview-notification-frame");
    private final SelenideElement alertOfferLaterBtn = $("#NC_CTA_TWO");
    private final SelenideElement closeAdPanelButton = $("button.MuiButtonBase-root[aria-label='close']");
    private final ElementsCollection flightInfoCollection = $$x("//p[contains(@class, 'MuiTypography-root') and @variantmd='h3']");
    private final ElementsCollection soldOutTicketCollection = $$x("//p[contains(text(), 'Sold out')]");
    private final ElementsCollection availableTicketCollection = $$x("//p[contains(text(), '000 VND')]/preceding-sibling::p");

    private final SelenideElement selectingDate = $("div[class*='lick-current'] p[weight='Bold']");
    private final SelenideElement continueButton = $x("//button[.//span[text()='Continue']]");

    //Dynamic Locators
    private final String flightPrice = "//p[contains(text(),'%s')]/parent::div//h4";

    //Variable
    private final String flightCardAdditionalXpath = "./parent::div/parent::div/parent::div/preceding-sibling::div";
    public static final ThreadLocal<FlightCardDataHolder> filghtCardDataHolderThreadLocal = ThreadLocal.withInitial(FlightCardDataHolder::new);

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

    @Step("Select flight type")
    public void closeAdPanelButton(){
        clickWhenReady(closeAdPanelButton);
    }

    @Step("Verify that the travel options page is displayed")
    public void verifyTravelOptionPageDisplayed(){
        webdriver().shouldHave(urlContaining("/select-flight"));
    }

    @Step("Verify currency on the travel options page")
    private void verifyCurrency(String currentcy) {
        // Locate any element that contains the text "VND"
        $$("p.MuiTypography-root").findBy(text(currentcy)).shouldBe(visible);
        availableTicketCollection.shouldHave(sizeGreaterThan(0));
    }

    @Step("Verify flight location")
    private void verifyFlightLocation(String expectedDepartCity, String expectedDestinationCity){
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

        if (departureCity != null && !departureCity.contains(expectedDepartCity)) {
            throw new AssertionError(String.format("Departure city mismatch: Expected %s but got %s", expectedDepartCity, departureCity));
        }

        if (destinationCity != null && !destinationCity.contains(expectedDestinationCity)) {
            throw new AssertionError(String.format("Destination city mismatch: Expected %s but got %s", expectedDestinationCity, destinationCity));
        }
    }

    @Step("Verify flight type and passenger information")
    private void verifyFlightTypeAndPassenger(String expectedFlightTypeAndPassenger){
        String flightTypeAndPassenger = null;

        for (int i = 0; i < flightInfoCollection.size(); i++) {
            String text = flightInfoCollection.get(i).getText().trim();
            // Check for flight type
            if (text.toLowerCase().contains("return") || text.toLowerCase().contains("one-way")) {
                flightTypeAndPassenger = text;
            }
        }

        if (flightTypeAndPassenger != null && !flightTypeAndPassenger.contains(expectedFlightTypeAndPassenger)) {
            throw new AssertionError(String.format("Flight type mismatch: Expected %s but got %s", expectedFlightTypeAndPassenger, flightTypeAndPassenger));
        }
    }

    @Step("Verify that there are no sold out tickets")
    private SelenideElement findCheapestTicket(){
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

    @Step("Select the lowest ticket")
    private void selectTicket(SelenideElement element){
        clickWhenReady(element);
    }

    @Step("Verify flight date on the travel options page")
    private void verifyFlightDate(LocalDate expectedLocalDate){
        String expectedDate = String.format("%s %d%s",
                expectedLocalDate.getMonth().getDisplayName(TextStyle.FULL, getLocale()),
                expectedLocalDate.getDayOfMonth(),
                getNumberSuffix(expectedLocalDate.getDayOfMonth()));

        selectingDate.shouldHave(exactText(expectedDate));
    }

    @Step("Extract flight information from the ticket element")
    private FlightCardInfo extractFlightInfo(SelenideElement ticketElement, String flightType){
        String flightId = ticketElement.$x("./div//span[contains(text(), 'VJ')]/ancestor::div[1]").getText();
        String time  = ticketElement.$x("./div//span[contains(text(), 'To')]/ancestor::div[1]").getText();
        String price = $x(flightPrice.formatted(flightType)).getText();

        return new FlightCardInfo(flightId, time, price);
    }

    @Step("Verify flight information on the travel options page")
    public void verifyFlightInfo(FlightDataObject data){
        verifyTravelOptionPageDisplayed();

        //verify currency
        verifyCurrency("VND");

        //verify flight depart and return address
        String expectedDepartAddress = String.format("%s%s", data.getDepartmentLocation(), data.getDepartmentLocationCode());
        String expectedDestinationAddress = String.format("%s%s", data.getDestinationLocation(), data.getDestinationLocationCode());
        verifyFlightLocation(expectedDepartAddress, expectedDestinationAddress);

        //verify flight type and passenger
        String expectedFlightTypeAndPassenger = String.format("%s | %s", data.getFlightTypeCode(),
                data.getFlightPassengerDataObject().getStringFlightPassenger());
        verifyFlightTypeAndPassenger(expectedFlightTypeAndPassenger);

        //verify depart date
        LocalDate expectedDepartLocalDate = LocalDate.now().plusDays(data.getDepartAfterDays());
        verifyFlightDate(expectedDepartLocalDate);

        //select lowest ticket and save the flight card info
        SelenideElement lowestDepart = findCheapestTicket();
        SelenideElement departFlightCard = lowestDepart.$x(flightCardAdditionalXpath);
        selectTicket(lowestDepart);
        filghtCardDataHolderThreadLocal.get().setDepartFlight(extractFlightInfo(departFlightCard, LanguageManager.get("departure_flight")));

        continueButton.click();

        //verify return date
        LocalDate expectedReturnLocalDate = expectedDepartLocalDate.plusDays(data.getReturnAfterDays());
        verifyFlightDate(expectedReturnLocalDate);

        //select lowest ticket and save the flight card info
        SelenideElement lowestReturn = findCheapestTicket();
        SelenideElement returnFlightCard = lowestReturn.$x(flightCardAdditionalXpath);
        selectTicket(lowestReturn);
        filghtCardDataHolderThreadLocal.get().setReturnFlight(extractFlightInfo(returnFlightCard, LanguageManager.get("return_flight")));
    }

    @Step("Continue to passenger info page")
    public void continueToPassengerPage(){
        //continue to passenger info page
        clickWhenReady(continueButton);
    }

}
