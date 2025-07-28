package pageObjects.VJPageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import testDataObject.VJTest.FlightCardInfo;
import testDataObject.VJTest.FlightCardDataHolder;
import testDataObject.VJTest.FlightPassengerDataObject;
import utils.LanguageManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.CollectionCondition.sizeGreaterThan;
import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.*;
import static com.codeborne.selenide.Condition.exactText;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.ElementHelper.clickWhenReady;
import static utils.ElementHelper.isElementDisplayed;
import static utils.ElementHelper.scrollToElement;
import static utils.ElementHelper.switchToDefault;
import static utils.ElementHelper.switchToIframe;
import static utils.ValueHelper.getNumberSuffix;
import static utils.ValueHelper.parsePrice;
import static utils.LanguageManager.getLocale;

public class VJSelectTicketPage {
    //Locators
    private final SelenideElement alertOfferIframe = $("#preview-notification-frame");
    private final SelenideElement alertOfferLaterBtn = $("#NC_CTA_TWO");
    private final SelenideElement closeAdPanelButton = $("button.MuiButtonBase-root[aria-label='close']");
    private final ElementsCollection flightInfoCollection = $$x("//p[contains(@class, 'MuiTypography-root')][@variantmd='h3']");
    private final ElementsCollection soldOutTicketCollection = $$x("//p[contains(text(), 'Sold out')]");
    private final ElementsCollection availableTicketCollection = $$x("//div[p[contains(text(), '000 VND')]]/p[contains(@class, 'MuiTypography-h4')]");

    private final SelenideElement selectingDate = $("div[class*='lick-current'] p[weight='Bold']");
    private final SelenideElement continueButton = $x(String.format("//button[.//span[text()='%s']]", LanguageManager.get("continue")));

    //Dynamic Locators
    private final String flightPrice = "//div[p[contains(text(),'%s')]]//h4";
    private final String flightIdAdditionalXpath = "./div//span[contains(text(), 'VJ')]/ancestor::div[1]";
    private final String timeAdditionalXpath = String.format("./div//span[contains(text(), '%s')]/ancestor::div[1]", LanguageManager.get("time_to"));

    //Variable
    private final String flightCardAdditionalXpath = "ancestor::div[3]/preceding-sibling::div";
    public static final ThreadLocal<FlightCardDataHolder> filghtCardDataHolderThreadLocal = ThreadLocal.withInitial(FlightCardDataHolder::new);

    //methods
    /**
     * Close the offer alert if it is displayed.
     * This method switches to the iframe containing the alert and clicks the "Later" button if it is visible.
     */
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

    /**
     * Close the advertisement panel if it is displayed.
     * This method checks if the close button for the ad panel is visible and clicks it.
     */
    @Step("Select flight type")
    public void closeAdPanelButton(){
        if (isElementDisplayed(closeAdPanelButton) ) {
            clickWhenReady(closeAdPanelButton);
        }
    }

    /**
     * Verify that the travel options page is displayed by checking the URL.
     * This method uses Selenide's webdriver to assert that the current URL contains "/select-flight".
     */
    @Step("Verify that the travel options page is displayed")
    public void verifyTravelOptionPageDisplayed(){
        webdriver().shouldHave(urlContaining("/select-flight"));
    }

    @Step("Verify currency on the travel options page")
    private void verifyCurrency(String currency) {
        // Locate any element that contains the text "VND"
        $$("p.MuiTypography-root").findBy(text(currency)).shouldBe(visible, Duration.ofSeconds(5));
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
        int lowestPrice = parsePrice(lowest.getText().trim());
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
        Locale locale = getLocale();
        String formatPattern = LanguageManager.get("date_display_format");

        String expectedDate = "";
        switch (locale.toString().toLowerCase()){
            case "vi-vn" -> expectedDate = expectedLocalDate.format(DateTimeFormatter.ofPattern(formatPattern, locale));
            case "en-us" -> {
                String suffix = getNumberSuffix(expectedLocalDate.getDayOfMonth());
                String patternWithSuffix = String.format(formatPattern, suffix);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternWithSuffix, locale);
                expectedDate = expectedLocalDate.format(formatter);
            }
            default -> { //fallback for other locales is en-us
                String suffix = getNumberSuffix(expectedLocalDate.getDayOfMonth());
                String patternWithSuffix = String.format(formatPattern, suffix);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern(patternWithSuffix, locale);
                expectedDate = expectedLocalDate.format(formatter);
            }
        }

        selectingDate.shouldHave(exactText(expectedDate));
    }

    @Step("Extract flight information from the ticket element")
    private FlightCardInfo extractFlightInfo(SelenideElement ticketElement, String flightType){
        String flightId = ticketElement.$x(flightIdAdditionalXpath).getText();
        String time  = ticketElement.$x(timeAdditionalXpath).getText();
        String price = $x(flightPrice.formatted(flightType)).getText();

        return new FlightCardInfo(flightId, time, price);
    }

    /**
     * Verify flight information on the travel options page.
     *
     * @param departAddress The departure address.
     * @param destinationAddress The destination address.
     * @param flightTypeCode The flight type code (e.g., "One-way", "Return").
     * @param flightPassengerDataObject The flight passenger data object containing passenger details.
     * @param departLocalDate The departure date.
     * @param returnLocalDate The return date (if applicable).
     */
    @Step("Verify flight information on the travel options page")
    public void selectTicket(String departAddress, String destinationAddress, String flightTypeCode
            , FlightPassengerDataObject flightPassengerDataObject, LocalDate departLocalDate, LocalDate returnLocalDate) {
        verifyTravelOptionPageDisplayed();

        //close offer alert if displayed
        closeAdPanelButton();

        //verify currency
        verifyCurrency("VND");

        //verify flight depart and return address
        verifyFlightLocation(departAddress, destinationAddress);

        //verify flight type and passenger
        String expectedFlightTypeAndPassenger = String.format("%s | %s", flightTypeCode,
                flightPassengerDataObject.getStringFlightPassenger());
        verifyFlightTypeAndPassenger(expectedFlightTypeAndPassenger);

        //verify depart date
        verifyFlightDate(departLocalDate);

        //select lowest ticket and save the flight card info
        SelenideElement lowestDepart = findCheapestTicket();
        SelenideElement departFlightCard = lowestDepart.$x(flightCardAdditionalXpath);
        selectTicket(lowestDepart);
        filghtCardDataHolderThreadLocal.get().setDepartFlight(extractFlightInfo(departFlightCard, LanguageManager.get("departure_flight")));

        continueButton.click();

        //verify return date
        verifyFlightDate(returnLocalDate);

        //select lowest ticket and save the flight card info
        SelenideElement lowestReturn = findCheapestTicket();
        SelenideElement returnFlightCard = lowestReturn.$x(flightCardAdditionalXpath);
        selectTicket(lowestReturn);
        filghtCardDataHolderThreadLocal.get().setReturnFlight(extractFlightInfo(returnFlightCard, LanguageManager.get("return_flight")));
    }

    /**
     * Proceed to the passenger information page after selecting flights.
     */
    @Step("Continue to passenger info page")
    public void continueToPassengerPage(){
        //continue to passenger info page
        clickWhenReady(continueButton);
    }

}
