package testcases.VJTest;

import driver.DriverFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.VJPageObjects.VJHomePage;
import pageObjects.VJPageObjects.VJPassengerInputPage;
import pageObjects.VJPageObjects.VJSelectFlightCheapPage;
import pageObjects.VJPageObjects.VJSelectTicketPage;
import testDataObject.VJTest.CheapestTicketDate;
import testDataObject.VJTest.FlightDataObject;
import testcases.TestBase;
import utils.RetryAnalyzer;
import utils.TestListener;
import io.qameta.allure.Description;

import java.time.LocalDate;

@Listeners({TestListener.class})
public class VJTestSel3 extends TestBase{
    VJHomePage homePage = new VJHomePage();
    VJSelectTicketPage selectTicketPage = new VJSelectTicketPage();
    VJPassengerInputPage passengerInputPage = new VJPassengerInputPage();
    VJSelectFlightCheapPage selectFlightCheapPage = new VJSelectFlightCheapPage();

    @Description("Validate flight booking flow on VietJet")
    @Test(dataProvider = "getData", description = "Search and choose tickets on a specific day successfully",
            retryAnalyzer = RetryAnalyzer.class, groups = {"VJ_EN_Regression", "FullRegression"})
    public void VJ_TC01_SearchCheapestTicketsInDayTest(FlightDataObject data) {
        logHelper.logStep("Step #1: Navigate to VietJet Air site");
        DriverFactory.openHomePage();

        logHelper.logStep("Step #2: Close banners and alert if present");
        homePage.closeCookiesBanner();
        homePage.closeOfferAlert();

        logHelper.logStep("Step #3: Fill search form and search for ticket");
        homePage.searchTicket(data);

        logHelper.logStep("Step #4: Close banners and alert if present");
        selectTicketPage.closeOfferAlert();
        selectTicketPage.closeAdPanelButton();

        logHelper.logStep("Step #5: Verify travel options page displayed");
        selectTicketPage.verifyTravelOptionPageDisplayed();

        logHelper.logStep("Step #6: Verify flight information");
        String expectedDepartAddress = String.format("%s%s", data.getDepartmentLocation(), data.getDepartmentLocationCode());
        String expectedDestinationAddress = String.format("%s%s", data.getDestinationLocation(), data.getDestinationLocationCode());
        LocalDate expectedDepartLocalDate = LocalDate.now().plusDays(data.getDepartAfterDays());
        LocalDate expectedReturnLocalDate = expectedDepartLocalDate.plusDays(data.getReturnAfterDays());

        selectTicketPage.selectTicket(expectedDepartAddress, expectedDestinationAddress, data.getFlightTypeCode(),
                data.getFlightPassengerDataObject(), expectedDepartLocalDate, expectedReturnLocalDate);

        logHelper.logStep("Step #7: Continue to Passenger page");
        selectTicketPage.continueToPassengerPage();

        logHelper.logStep("Step #8: Verify passenger info page displayed");
        passengerInputPage.verifyPassengerPageDisplayed();

        logHelper.logStep("Step #9: Verify flight ticket info correct");
        passengerInputPage.verifyTicketInfo(data.getDepartmentLocation(), data.getDestinationLocation(),
                expectedDepartLocalDate, expectedReturnLocalDate);
    }

    @Description("Search and choose cheapest tickets on next 3 months successfully")
    @Test(dataProvider = "getData", description = "Search and choose cheapest tickets on next 3 months successfully",
            retryAnalyzer = RetryAnalyzer.class, groups = {"VJ_VI_Regression", "FullRegression"})
    public void VJ_TC02_SearchCheapestTicketsInNextThreeMonthsTest(FlightDataObject data) {
        logHelper.logStep("Step #1: Navigate to VietJet Air site");
        DriverFactory.openHomePage();

        logHelper.logStep("Step #2: Navigate to VietJet Air site");
        homePage.verifyPageDisplayInVietnamese();

        logHelper.logStep("Step #3: Close banners and alert if present");
        homePage.closeCookiesBanner();
        homePage.closeOfferAlert();

        logHelper.logStep("Step #4: Fill search form and search for ticket");
        homePage.searchTicketWithLowestOption(data);

        logHelper.logStep("Step #5: Verify travel options page displayed");
        selectFlightCheapPage.verifySelectFlightCheapPageDisplayed();

        logHelper.logStep("Step #6: Select cheapest month flight ticket");
        CheapestTicketDate cheapestDate = selectFlightCheapPage.selectCheapestTicketDates(
                data.getDepartAfterMonths(), data.getReturnAfterMonths(), data.getReturnAfterDays());
        selectFlightCheapPage.clickContinueButton();

        logHelper.logStep("Step #7: Close banners and alert if present");
        homePage.closeCookiesBanner();
        homePage.closeOfferAlert();

        logHelper.logStep("Step #8: Verify travel options page displayed");
        selectTicketPage.verifyTravelOptionPageDisplayed();

        logHelper.logStep("Step #9: Verify flight information");
        String expectedDepartAddress = String.format("%s%s", data.getDepartmentLocation(), data.getDepartmentLocationCode());
        String expectedDestinationAddress = String.format("%s%s", data.getDestinationLocation(), data.getDestinationLocationCode());
        LocalDate expectedDepartLocalDate = cheapestDate.getDepartDate();
        LocalDate expectedReturnLocalDate = cheapestDate.getReturnDate();

        selectTicketPage.selectTicket(expectedDepartAddress, expectedDestinationAddress, data.getFlightTypeCode(),
                data.getFlightPassengerDataObject(), expectedDepartLocalDate, expectedReturnLocalDate);

        logHelper.logStep("Step #10: Continue to Passenger page");
        selectTicketPage.continueToPassengerPage();

        logHelper.logStep("Step #11: Verify passenger info page displayed");
        passengerInputPage.verifyPassengerPageDisplayed();

        logHelper.logStep("Step #12: Verify flight ticket info correct");
        passengerInputPage.verifyTicketInfo(data.getDepartmentLocation(), data.getDestinationLocation(),
                expectedDepartLocalDate, expectedReturnLocalDate);
    }
}
