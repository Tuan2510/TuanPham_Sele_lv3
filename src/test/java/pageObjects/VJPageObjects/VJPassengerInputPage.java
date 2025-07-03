package pageObjects.VJPageObjects;

import com.codeborne.selenide.SelenideElement;
import utils.ElementHelper;
import utils.LanguageManager;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static pageObjects.VJPageObjects.VJSelectTicketPage.filghtCardDataHolderThreadLocal;
import static utils.LanguageManager.getLocale;

public class VJPassengerInputPage {


    // Dynamic Locators
    private final String flightLocationXpath = "//p[contains(text(),'%s')]/parent::div/following-sibling::div//img/ancestor::div[1]";
    private final String flightDateXpath = "//p[contains(text(),'%s')]/parent::div/following-sibling::div//h5[not(@variantlg='h4')]";
    private final String flightPrice = "//p[contains(text(),'%s')]/parent::div//h4";

    public void verifyTravelOptionPageDisplayed(){
        //check the url
        webdriver().shouldHave(urlContaining("/select-flight"));
    }

    public void verifyDepartAndReturnLocation(String departLocation, String returnLocation){
        SelenideElement flightDepartLocations = $x(flightLocationXpath.formatted("Depart"));
        flightDepartLocations.shouldHave(text(departLocation));
        flightDepartLocations.shouldHave(text(returnLocation));

        SelenideElement flightReturnLocations = $x(flightLocationXpath.formatted("Return"));
        flightReturnLocations.shouldHave(text(departLocation));
        flightReturnLocations.shouldHave(text(returnLocation));
    }

    public void verifyFlightDate(LocalDate departLocalDate, LocalDate returnLocalDate){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE, dd/MM/yyyy", getLocale());
        String departDate = departLocalDate.format(formatter);
        String returnDate = returnLocalDate.format(formatter);

        SelenideElement flightDepartDate = $x(flightDateXpath.formatted("Depart"));
        flightDepartDate.shouldHave(text(departDate));

        SelenideElement flightReturnDate = $x(flightDateXpath.formatted("Return"));
        flightReturnDate.shouldHave(text(returnDate));
    }

    public void verifyFlightIdAndTime(String departFlightId, String departTime, String returnFlightId, String returnTime){
        SelenideElement flightDepartDate = $x(flightDateXpath.formatted("Depart"));
        flightDepartDate.shouldHave(text(departFlightId));
        flightDepartDate.shouldHave(text(departTime));

        SelenideElement flightReturnDate = $x(flightDateXpath.formatted("Return"));
        flightReturnDate.shouldHave(text(returnFlightId));
        flightReturnDate.shouldHave(text(returnTime));
    }

    public void verifyFlightPrice(String departPrice, String returnPrice){
        SelenideElement flightDepartPrice = $x(flightPrice.formatted("Depart"));
        flightDepartPrice.shouldHave(text(departPrice));

        SelenideElement flightReturnPrice = $x(flightPrice.formatted("Return"));
        flightReturnPrice.shouldHave(text(returnPrice));
    }

    public void verifyTicketInfo(String departLocation, String returnLocation, int departAfterDays, int returnAfterDate){
        ElementHelper.scrollToPageTop();

        verifyDepartAndReturnLocation(departLocation, returnLocation);

        LocalDate departLocalDate = LocalDate.now().plusDays(departAfterDays);
        LocalDate returnLocalDate = departLocalDate.plusDays(returnAfterDate);
        verifyFlightDate(departLocalDate, returnLocalDate);

        String departFlightId = filghtCardDataHolderThreadLocal.get().getDepartFlight().getFlightId();
        String departTime = filghtCardDataHolderThreadLocal.get().getDepartFlight().getTime().replace("To", "-");
        String returnFlightId = filghtCardDataHolderThreadLocal.get().getReturnFlight().getFlightId();
        String returnTime = filghtCardDataHolderThreadLocal.get().getReturnFlight().getTime().replace("To", "-");
        verifyFlightIdAndTime(departFlightId, departTime, returnFlightId, returnTime);

        String departPrice = filghtCardDataHolderThreadLocal.get().getDepartFlight().getPrice();
        String returnPrice = filghtCardDataHolderThreadLocal.get().getReturnFlight().getPrice();
        verifyFlightPrice(departPrice, returnPrice);

    }

}
