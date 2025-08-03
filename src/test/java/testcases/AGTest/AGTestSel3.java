package testcases.AGTest;

import driver.DriverFactory;
import io.qameta.allure.Description;
import org.testng.ITestContext;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.AGPageObjects.AgodaHomePage;
import pageObjects.AGPageObjects.AgodaHotelDetailsPage;
import pageObjects.AGPageObjects.AgodaSearchResultsPage;
import testDataObject.AGTest.AGDataObject;
import testDataObject.AGTest.Hotel;
import testDataObject.AGTest.PriceFilter;
import testcases.TestBase;
import utils.RetryAnalyzer;
import utils.TestListener;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;
import java.util.Map;

@Listeners({TestListener.class})
public class AGTestSel3 extends TestBase {
    AgodaHomePage agodaHomePage;
    AgodaSearchResultsPage agodaSearchResultsPage;
    AgodaHotelDetailsPage agodaHotelDetailsPage;
    String checkInDayOfWeek = "FRIDAY";
    int stayDurationDays = 3;

    @Override
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Object[] testArgs, ITestContext context) {
        super.beforeMethod(testArgs, context);
        agodaHomePage = new AgodaHomePage();
        agodaSearchResultsPage = new AgodaSearchResultsPage();
        agodaHotelDetailsPage = new AgodaHotelDetailsPage();
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

    @Description("Search, filter and verify hotel details successfully")
    @Test(dataProvider = "getData", description = "Search, filter and verify hotel details successfully",
            retryAnalyzer = RetryAnalyzer.class, groups = {"AG_Regression", "FullRegression"})
    public void AG_TC03_SearchAndAddVerifyHotelInfo(AGDataObject data) {
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

        logHelper.logStep("Step #4: Filter the swimming hotels and choose the 5th hotel in the list");
        agodaSearchResultsPage.filterByFacilities(data.getFacilities());
        Hotel fifthHotel = agodaSearchResultsPage.openHotelDetailsByIndex(5);
        agodaHotelDetailsPage.verifyHotelInfoAndFacilities(fifthHotel, data.getFacilities());

        logHelper.logStep("Step #5: Back to the filter page");
        agodaHotelDetailsPage.goBackToSearchResultsPage();

        logHelper.logStep("Step #6: Move mouse to the point of the 1st hotel to show detailed review points");
        List<String> reviewCategories = data.getReviewCategories();
        Map<String, String> reviewScores = agodaSearchResultsPage.getHotelReviewScores(1, reviewCategories);

        logHelper.logStep("Step #7: Choose the first hotel The hotel detailed page is displayed with correct info");
        Hotel firstHotel = agodaSearchResultsPage.openHotelDetailsByIndex(1);
        agodaHotelDetailsPage.verifyHotelInfoAndFacilities(firstHotel, data.getFacilities());
        agodaHotelDetailsPage.verifyReviewScores(reviewScores);
    }

}
