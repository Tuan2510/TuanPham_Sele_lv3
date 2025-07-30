package pageObjects.VJPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utils.ElementHelper;
import utils.LanguageManager;
import utils.LogHelper;
import utils.TestListener;

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
    private static final Logger logger = LoggerFactory.getLogger(VJPassengerInputPage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    // Dynamic Locators
    private final String flightLocationXpath = "//div[div/p[contains(text(),'%s')]]//div[span/img]";
    private final String flightDateXpath = "//div[div/p[contains(text(),'%s')]]//h5[not(@variantlg='h4')]";
    private final String flightPrice = "//div[p[contains(text(),'%s')]]//h4";

    private SelenideElement getFlightDateSection(String type) {
        return $x(flightDateXpath.formatted(type));
    }

    /**
     * Verifies that the passenger input page is displayed by checking the URL.
     * This method uses Selenide's webdriver to assert that the current URL contains "/passengers".
     */
    public void verifyPassengerPageDisplayed(){
        //check the url
        webdriver().shouldHave(urlContaining("/passengers"));
    }

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

    private void verifyFlightDate(LocalDate departLocalDate, LocalDate returnLocalDate){
        String departDate = formatShortDay(departLocalDate, LanguageManager.get("full_local_date_format"));
        String returnDate = formatShortDay(returnLocalDate, LanguageManager.get("full_local_date_format"));

        logHelper.logStep("Departure date: %s, Return date: %s", departDate, returnDate);
        SelenideElement flightDepartDate = getFlightDateSection(LanguageManager.get("departure_flight"));
        flightDepartDate.shouldHave(text(departDate));

        SelenideElement flightReturnDate = getFlightDateSection(LanguageManager.get("return_flight"));
        flightReturnDate.shouldHave(text(returnDate));
    }

    private void verifyFlightIdAndTime(String departFlightId, String departTime, String returnFlightId, String returnTime){
        logHelper.logStep("Departure Flight ID: %s, Departure Time: %s, Return Flight ID: %s, Return Time: %s",
                departFlightId, departTime, returnFlightId, returnTime);
        SelenideElement flightDepartDate = getFlightDateSection(LanguageManager.get("departure_flight"));
        flightDepartDate.shouldHave(text(departFlightId));
        flightDepartDate.shouldHave(text(departTime));

        SelenideElement flightReturnDate = getFlightDateSection(LanguageManager.get("return_flight"));
        flightReturnDate.shouldHave(text(returnFlightId));
        flightReturnDate.shouldHave(text(returnTime));
    }

    private void verifyFlightPrice(String departPrice, String returnPrice){
        logHelper.logStep("Departure Price: %s, Return Price: %s", departPrice, returnPrice);
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
    public void verifyTicketInfo(String departLocation, String returnLocation
            , LocalDate expectedDepartLocalDate, LocalDate expectedReturnLocalDate){
        logHelper.logStep("Verifying ticket information for departure location: %s, return location: %s"
                , departLocation, returnLocation);
        ElementHelper.scrollToPageTop();

        verifyDepartAndReturnLocation(departLocation, returnLocation);

        logHelper.logStep("Verifying flight dates for departure: %s, return: %s"
                , expectedDepartLocalDate.toString(), expectedReturnLocalDate.toString());
        verifyFlightDate(expectedDepartLocalDate, expectedReturnLocalDate);

        String departFlightId = filghtCardDataHolderThreadLocal.get().getDepartFlight().getFlightId();
        String departTime = filghtCardDataHolderThreadLocal.get().getDepartFlight().getTime().replace(LanguageManager.get("time_to"), "-");
        String returnFlightId = filghtCardDataHolderThreadLocal.get().getReturnFlight().getFlightId();
        String returnTime = filghtCardDataHolderThreadLocal.get().getReturnFlight().getTime().replace(LanguageManager.get("time_to"), "-");
        logHelper.logStep("Verifying flight departFlightId: %s, departTime: %s, returnFlightId: %s, returnTime: %s"
                , filghtCardDataHolderThreadLocal.get().getDepartFlight().getFlightId()
                , filghtCardDataHolderThreadLocal.get().getDepartFlight().getTime()
                , filghtCardDataHolderThreadLocal.get().getReturnFlight().getFlightId()
                , filghtCardDataHolderThreadLocal.get().getReturnFlight().getTime());
        verifyFlightIdAndTime(departFlightId, departTime, returnFlightId, returnTime);

        String departPrice = filghtCardDataHolderThreadLocal.get().getDepartFlight().getPrice();
        String returnPrice = filghtCardDataHolderThreadLocal.get().getReturnFlight().getPrice();
        logHelper.logStep("Verifying flight prices for departPrice: %s, returnPrice: %s"
                , departPrice, returnPrice);
        verifyFlightPrice(departPrice, returnPrice);
    }
}
