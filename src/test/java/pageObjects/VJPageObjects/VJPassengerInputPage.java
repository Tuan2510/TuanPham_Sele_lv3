package pageObjects.VJPageObjects;

import static com.codeborne.selenide.WebDriverRunner.url;
import static org.testng.Assert.assertTrue;

public class VJPassengerInputPage {

    public void verifyTravelOptionPageDisplayed(){
        assertTrue(url().contains("/passenger"), "Passenger page is not displayed.");
    }
}
