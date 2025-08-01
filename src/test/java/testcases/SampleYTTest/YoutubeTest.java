package testcases.SampleYTTest;

import driver.DriverFactory;
import testcases.TestBase;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.YouTubeHomePage;
import utils.RetryAnalyzer;
import testDataObject.SampleDataObject;
import utils.TestListener;

@Listeners({TestListener.class})
public class YoutubeTest extends TestBase {
    YouTubeHomePage youTubeHomePage = new YouTubeHomePage();

    @Test(dataProvider = "getData", retryAnalyzer = RetryAnalyzer.class, groups = "TestRegression")
    public void TC01(SampleDataObject testData) {
        logHelper.logStep("Step #1: Navigate to YouTube");
        DriverFactory.openHomePage();

        logHelper.logStep(String.format("Step #2: Search for %s", testData.getQuery()));
        youTubeHomePage.search(testData.getQuery());

        logHelper.logStep("Step #3: Check for result");
        youTubeHomePage.verifyResultsVisible();
    }

}
