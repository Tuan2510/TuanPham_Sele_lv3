package testcases.AGTest;

import driver.DriverFactory;
import io.qameta.allure.Description;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.AGPageObjects.AgodaFlightSearchPage;
import pageObjects.AGPageObjects.AgodaHomePage;
import pageObjects.AGPageObjects.AgodaHotelDetailsPage;
import pageObjects.AGPageObjects.AgodaSearchResultsPage;
import testDataObject.AGTest.AGDataObject;
import testDataObject.AGTest.Hotel;
import testDataObject.AGTest.PriceFilter;
import testDataObject.AGTest.ReviewCategory;
import testcases.TestBase;
import utils.RetryAnalyzer;
import utils.TestListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.Map;

@Listeners({TestListener.class})
public class AGTestSel3 extends TestBase {
    AgodaHomePage agodaHomePage;
    AgodaSearchResultsPage agodaSearchResultsPage;
    AgodaHotelDetailsPage agodaHotelDetailsPage;
    AgodaFlightSearchPage agodaFlightSearchPage;
    LocalDate checkInDate;
    LocalDate checkOutDate;
    LocalDate flightDepartureDate;
    PriceFilter defaultPriceFilter;
    Hotel hotel;

    @Override
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Object[] testArgs, ITestContext context) {
        super.beforeMethod(testArgs, context);
        agodaHomePage = new AgodaHomePage();
        agodaSearchResultsPage = new AgodaSearchResultsPage();
        agodaHotelDetailsPage = new AgodaHotelDetailsPage();
        agodaFlightSearchPage = new AgodaFlightSearchPage();

        checkInDate = LocalDate.now().plusDays(1);
        checkOutDate = checkInDate.plusDays(3);
        flightDepartureDate = LocalDate.now().with(TemporalAdjusters.firstDayOfNextMonth());
    }

    @Description("Search and filter hotels successfully")
    @Test(dataProvider = "getData", description = "Search and filter for hotels successfully",
            retryAnalyzer = RetryAnalyzer.class, groups = {"AG_Regression", "FullRegression"})
    public void AG_TC01_SearchAndFilterHotelsWithBreakfast(AGDataObject data) {
        logHelper.logStep("Step #1: Navigate to Agoda site");
        DriverFactory.openHomePage();

        logHelper.logStep("Step #2: Search hotel with test case information");

        agodaHomePage.searchHotel(
                data.getPlace(),
                checkInDate,
                checkOutDate,
                data.getOccupancy());

        logHelper.logStep("Step #3: Search result is displayed correctly with first %s hotels in %s",
                data.getResultCount(), data.getPlace());
        agodaSearchResultsPage.verifyPageIsDisplayed();
        agodaSearchResultsPage.verifySearchResultsHotelAddress(data.getResultCount(), data.getPlace());

        logHelper.logStep("Step #4: Filter the hotels with breakfast included and select the first hotel");
        agodaSearchResultsPage.filterByFacilities(data.getFacilities());
        hotel = agodaSearchResultsPage.openHotelDetailsByIndex(1);

        logHelper.logStep("Step #5: Verify hotel details and facilities");
        agodaHotelDetailsPage.verifyHotelInfoAndFacilities(hotel, data.getFacilities());
    }

    @Description("Search and add flight to cart successfully")
    @Test(dataProvider = "getData", description = "Search and add flight to cart successfully",
            retryAnalyzer = RetryAnalyzer.class, groups = {"AG_Regression", "FullRegression"})
    public void AG_TC02_SearchAndAddFlightToCart(AGDataObject data) {
        logHelper.logStep("Step #1: Navigate to Agoda site");
        DriverFactory.openHomePage();

        logHelper.logStep("Step #2: goto the flight page");
        agodaHomePage.goToFlightPage();

        logHelper.logStep("Step #3: Search flight with test case information");
        agodaHomePage.searchOneWayFlight(
                data.getFlightOrigin(),
                data.getFlightDestination(),
                flightDepartureDate,
                data.getFlightClass(),
                data.getFlightOccupancy());

        logHelper.logStep("Step #4: Verify search results are displayed correctly");
        agodaFlightSearchPage.verifyPageIsDisplayed();

        logHelper.logStep("Step #5: Apply sort by fastest flights");
        agodaFlightSearchPage.applySortByFastest();

        logHelper.logStep("Step #6: Verify that first %s flight durations are sorted by fastest", data.getResultCount());
        agodaFlightSearchPage.verifyResultsSortedByFastest(data.getResultCount());

        logHelper.logStep("Step #7: Select the first flight and add it to cart");
        agodaFlightSearchPage.expandFlightByIndex(1);
        agodaFlightSearchPage.addFlightToCart();

        logHelper.logStep("Step #8: Verify flight info in cart");
    }

}
