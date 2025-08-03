package testcases.AGTest;

import driver.DriverFactory;
import io.qameta.allure.Description;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.AGPageObjects.AgodaHomePage;
import pageObjects.AGPageObjects.AgodaSearchResultsPage;
import testDataObject.AGTest.AGDataObject;
import testDataObject.AGTest.PriceFilter;
import testcases.TestBase;
import utils.RetryAnalyzer;
import utils.TestListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;

@Listeners({TestListener.class})
public class AGTestSel3 extends TestBase {
    AgodaHomePage agodaHomePage;
    AgodaSearchResultsPage agodaSearchResultsPage;
    String checkInDayOfWeek = "FRIDAY";
    int stayDurationDays = 3;

    @Override
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Object[] testArgs, ITestContext context) {
        super.beforeMethod(testArgs, context);
        agodaHomePage = new AgodaHomePage();
        agodaSearchResultsPage = new AgodaSearchResultsPage();
    }

    @Description("Search and sort hotel successfully")
    @Test(dataProvider = "getData", description = "Search and sort for hotel successfully",
            retryAnalyzer = RetryAnalyzer.class, groups = {"AG_Regression", "FullRegression"})
    public void AG_TC01_SearchAndSortHotel(AGDataObject data) {
        logHelper.logStep("Step #1: Navigate to Agoda site");
        DriverFactory.openHomePage();

        logHelper.logStep("Step #2: Search hotel with test case information");
        LocalDate checkInDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.valueOf(checkInDayOfWeek)));
        LocalDate checkOutDate = checkInDate.plusDays(stayDurationDays);

        agodaHomePage.searchHotel(
                data.getPlace(),
                checkInDate,
                checkOutDate,
                data.getOccupancy());

        logHelper.logStep("Step #3: Verify search results are displayed correctly with first 5 hotels in " + data.getPlace());
        agodaSearchResultsPage.verifyPageIsDisplayed();
        agodaSearchResultsPage.verifySearchResultsHotelAddress(data.getResultCount(), data.getPlace());

        logHelper.logStep("Step #4: Sort results by lowest price");
        agodaSearchResultsPage.sortByLowestPrice();

        logHelper.logStep("Step #5: Verify results are sorted by lowest price and the destination is still correct");
        agodaSearchResultsPage.verifyResultsSortedByLowestPrice(data.getResultCount());
        agodaSearchResultsPage.verifySearchResultsHotelAddress(data.getResultCount(), data.getPlace());
    }

    @Description("Search and sort hotel successfully")
    @Test(dataProvider = "getData", description = "Search and sort for hotel successfully",
            retryAnalyzer = RetryAnalyzer.class, groups = {"AG_Regression", "FullRegression"})
    public void AG_TC02_SearchAndFilterHotel(AGDataObject data) {
        logHelper.logStep("Step #1: Navigate to Agoda site");
        DriverFactory.openHomePage();

        logHelper.logStep("Step #2: Search hotel with test case information");
        LocalDate checkInDate = LocalDate.now().with(TemporalAdjusters.next(DayOfWeek.valueOf(checkInDayOfWeek)));
        LocalDate checkOutDate = checkInDate.plusDays(stayDurationDays);

        agodaHomePage.searchHotel(
                data.getPlace(),
                checkInDate,
                checkOutDate,
                data.getOccupancy());

        logHelper.logStep("Step #3: Verify search results are displayed correctly with first 5 hotels in " + data.getPlace());
        agodaSearchResultsPage.verifyPageIsDisplayed();
        agodaSearchResultsPage.verifySearchResultsHotelAddress(data.getResultCount(), data.getPlace());

        logHelper.logStep("Step #4: Filter results by price range and star rating");
        PriceFilter defaultPriceFilter = agodaSearchResultsPage.getFilterValues();
        agodaSearchResultsPage.setPriceFilter(data.getPriceFilter().getPriceMin(), data.getPriceFilter().getPriceMax());
        agodaSearchResultsPage.filterByStarRating(data.getRating());

        logHelper.logStep("Step #5: Verify results are filtered by price range and star rating");
        agodaSearchResultsPage.verifyFilterApplied(data.getPriceFilter());
        agodaSearchResultsPage.verifyHotelPriceAfterFilter(data.getResultCount(), data.getPriceFilter().getPriceMin(),
                data.getPriceFilter().getPriceMax());
        agodaSearchResultsPage.verifyHotelStarRatingAfterFilter(data.getResultCount(), data.getRating());

        logHelper.logStep("Step #6: Reset price filters");
        agodaSearchResultsPage.resetPriceFilter(defaultPriceFilter);
        agodaSearchResultsPage.verifyPriceFilterReset(defaultPriceFilter);
    }
}
