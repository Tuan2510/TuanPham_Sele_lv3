package testcases.VJTest;

import driver.DriverFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.VJPageObjects.VJHomePage;
import pageObjects.VJPageObjects.VJPassengerInputPage;
import pageObjects.VJPageObjects.VJSelectTicketPage;
import testDataObject.VJTest.FlightDataObject;
import testcases.TestBase;
import utils.RetryAnalyzer;
import utils.TestListener;

@Listeners({TestListener.class})
public class VJTestSel3 extends TestBase{
    VJHomePage homePage = new VJHomePage();
    VJSelectTicketPage selectTicketPage = new VJSelectTicketPage();
    VJPassengerInputPage passengerInputPage = new VJPassengerInputPage();

    @Test(dataProvider = "getData", retryAnalyzer = RetryAnalyzer.class, groups = "VJRegression, FullRegression")
    public void TC01(FlightDataObject data) {
        logHelper.logStep("Step #1: Navigate to VietJet Air site");
        DriverFactory.openHomePage();

        logHelper.logStep("Step #2: Close banners if present");
        homePage.closeCookiesBanner();
        homePage.closeOfferAlert();

        logHelper.logStep("Step #3: Fill search form");
        homePage.chooseFlightType("roundTrip");
        homePage.selectDepartureLocation(data.getDepartmentLocation());
        homePage.selectDestinationLocation(data.getDestinationLocation());
        homePage.selectRoundTripDate(data.getDepartAfterDays(), data.getReturnAfterDays());
        homePage.selectPassengerNumber(
                data.getFlightPassengerDataObject().getAdults(),
                data.getFlightPassengerDataObject().getChildren(),
                data.getFlightPassengerDataObject().getInfants());
        homePage.findTicket();

        logHelper.logStep("Step #4: Verify travel options page");




    }
}
