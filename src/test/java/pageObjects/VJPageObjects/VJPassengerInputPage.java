package pageObjects.VJPageObjects;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static pageObjects.VJPageObjects.VJSelectTicketPage.filghtCardDataHolderThreadLocal;

public class VJPassengerInputPage {


    // Dynamic Locators
    private final String flightLocationXpath = "//p[contains(text(),'%s')]/parent::div/following-sibling::div//img/ancestor::div[1]";

    public void verifyTravelOptionPageDisplayed(){
        //check the url
        webdriver().shouldHave(urlContaining("/select-flight"));
    }

    public void verifyTicketInfo(){

        System.out.println(filghtCardDataHolderThreadLocal.get().getDepartFlight().getFlightId() );
    }

}
