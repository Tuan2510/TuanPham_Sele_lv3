package pageObjects.VJPageObjects;

import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;

public class VJPassengerInputPage {

    public void verifyTravelOptionPageDisplayed(){
        //check the url
        webdriver().shouldHave(urlContaining("/select-flight"));
    }



}
