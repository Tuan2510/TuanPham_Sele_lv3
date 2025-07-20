package pageObjects.VJPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import utils.ElementHelper;
import utils.LanguageManager;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static pageObjects.VJPageObjects.VJSelectTicketPage.filghtCardDataHolderThreadLocal;
import static utils.DateHelper.formatShortDay;
import static utils.LanguageManager.getLocale;

public class VJPassengerInputPage {


    // Dynamic Locators
    private final String flightLocationXpath = "//div[div/p[contains(text(),'%s')]]//div[span/img]";
    private final String flightDateXpath = "//div[div/p[contains(text(),'%s')]]//h5[not(@variantlg='h4')]";
    private final String flightPrice = "//div[p[contains(text(),'%s')]]//h4";

    @Step("Get flight date section for type: {type}")
    private SelenideElement getFlightDateSection(String type) {
        return $x(flightDateXpath.formatted(type));
    }

    /**
     * Verifies that the passenger input page is displayed by checking the URL.
     * This method uses Selenide's webdriver to assert that the current URL contains "/passengers".
     */
    @Step("Verify that the passenger input page is displayed")
    public void verifyPassengerPageDisplayed(){
        //check the url
        webdriver().shouldHave(urlContaining("/passengers"));
    }

    @Step("Verify that the flight information is correct")
    private void verifyDepartAndReturnLocation(String departLocation, String returnLocation){
        SelenideElement flightDepartLocations = $x(flightLocationXpath.formatted(LanguageManager.get("departure_flight")));
        flightDepartLocations.shouldBe(Condition.visible, Duration.ofSeconds(5));
        flightDepartLocations.shouldHave(text(departLocation));
        flightDepartLocations.shouldHave(text(returnLocation));

        SelenideElement flightReturnLocations = $x(flightLocationXpath.formatted(LanguageManager.get("return_flight")));
        flightReturnLocations.shouldBe(Condition.visible, Duration.ofSeconds(5));
        flightReturnLocations.shouldHave(text(departLocation));
        flightReturnLocations.shouldHave(text(returnLocation));
    }

    @Step("Verify the flight date and time")
    private void verifyFlightDate(LocalDate departLocalDate, LocalDate returnLocalDate){
        String departDate = formatShortDay(departLocalDate, LanguageManager.get("full_local_date_format"));
        String returnDate = formatShortDay(returnLocalDate, LanguageManager.get("full_local_date_format"));

        SelenideElement flightDepartDate = getFlightDateSection(LanguageManager.get("departure_flight"));
        flightDepartDate.shouldHave(text(departDate));

        SelenideElement flightReturnDate = getFlightDateSection(LanguageManager.get("return_flight"));
        flightReturnDate.shouldHave(text(returnDate));
    }

    @Step("Verify the flight ID and time")
    private void verifyFlightIdAndTime(String departFlightId, String departTime, String returnFlightId, String returnTime){
        SelenideElement flightDepartDate = getFlightDateSection(LanguageManager.get("departure_flight"));
        flightDepartDate.shouldHave(text(departFlightId));
        flightDepartDate.shouldHave(text(departTime));

        SelenideElement flightReturnDate = getFlightDateSection(LanguageManager.get("return_flight"));
        flightReturnDate.shouldHave(text(returnFlightId));
        flightReturnDate.shouldHave(text(returnTime));
    }

    @Step("Verify the flight price for departure and return flights")
    private void verifyFlightPrice(String departPrice, String returnPrice){
        SelenideElement flightDepartPrice = $x(flightPrice.formatted(LanguageManager.get("departure_flight")));
        flightDepartPrice.shouldHave(text(departPrice));

        SelenideElement flightReturnPrice = $x(flightPrice.formatted(LanguageManager.get("return_flight")));
        flightReturnPrice.shouldHave(text(returnPrice));
    }

    /**
     * Verifies the ticket information displayed on the passenger input page.
     * This includes checking the flight locations, dates, IDs, times, and prices.
     *
     * @param departLocation The departure location for the flight.
     * @param returnLocation The return location for the flight.
     * @param expectedDepartLocalDate The expected departure date in LocalDate format.
     * @param expectedReturnLocalDate The expected return date in LocalDate format.
     */
    @Step("Verify the ticket information on the passenger input page")
    public void verifyTicketInfo(String departLocation, String returnLocation
            , LocalDate expectedDepartLocalDate, LocalDate expectedReturnLocalDate){
        ElementHelper.scrollToPageTop();

        verifyDepartAndReturnLocation(departLocation, returnLocation);

        verifyFlightDate(expectedDepartLocalDate, expectedReturnLocalDate);

        String departFlightId = filghtCardDataHolderThreadLocal.get().getDepartFlight().getFlightId();
        String departTime = filghtCardDataHolderThreadLocal.get().getDepartFlight().getTime().replace(LanguageManager.get("time_to"), "-");
        String returnFlightId = filghtCardDataHolderThreadLocal.get().getReturnFlight().getFlightId();
        String returnTime = filghtCardDataHolderThreadLocal.get().getReturnFlight().getTime().replace(LanguageManager.get("time_to"), "-");
        verifyFlightIdAndTime(departFlightId, departTime, returnFlightId, returnTime);

        String departPrice = filghtCardDataHolderThreadLocal.get().getDepartFlight().getPrice();
        String returnPrice = filghtCardDataHolderThreadLocal.get().getReturnFlight().getPrice();
        verifyFlightPrice(departPrice, returnPrice);
    }
}
