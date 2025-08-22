package pageObjects.AGPageObjects;


import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testDataObject.AGTest.FlightDisplayInfo;
import utils.LanguageManager;
import utils.LogHelper;
import utils.TestListener;

import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.LanguageManager.getLocale;

public class AgodaFlightCartPage {
    private static final Logger logger = LoggerFactory.getLogger(AgodaFlightCartPage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    private final SelenideElement flightDateTime = $("[data-component='FlightDate']");
    private final SelenideElement flightNameInfo = $("[data-component='FlightInfo']");
    private final SelenideElement flightClass = $("[data-component='CabinClassText']");

    public void verifyPageIsDisplayed() {
        webdriver().shouldHave(urlContaining("/cart"));
        logHelper.logStep("Search results page is displayed with URL: %s", webdriver().driver().getCurrentFrameUrl());
    }

    public void verifyFlightDetails(FlightDisplayInfo flightInfo) {
        logHelper.logStep("Verifying flight details in the cart");

        flightDateTime.shouldHave(Condition.text(flightInfo.getDate()
                .format(DateTimeFormatter.ofPattern(LanguageManager.get("flight_date_patten"), getLocale()))));
        flightDateTime.shouldHave(Condition.text(flightInfo.getDepartureTime()
                .format(DateTimeFormatter.ofPattern(LanguageManager.get("flight_time_pattern"), getLocale()))));
        flightNameInfo.shouldHave(Condition.text(flightInfo.getAirline()));
        flightClass.shouldHave(Condition.text(flightInfo.getFlightClass()));
    }
}
