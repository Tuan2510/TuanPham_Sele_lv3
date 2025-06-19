package testcases.SampleYTTest;

import base.TestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.YouTubeHomePage;
import utils.RetryAnalyzer;
import utils.TestData;
import utils.TestDataProvider;
import utils.TestListener;

import java.util.Hashtable;
import java.util.Map;

@Listeners({TestListener.class})
public class YoutubeTest extends TestBase {
    YouTubeHomePage youTubeHomePage = new YouTubeHomePage();

    @Test(dataProvider = "getData", dataProviderClass = TestDataProvider.class, retryAnalyzer = RetryAnalyzer.class, groups = "Regression")
    public void TC01(TestData[] data) {
        for ( TestData testData : data) {
            logHelper.logStep("Step #1: Navigate to YouTube");
            youTubeHomePage.openHome();

            logHelper.logStep("Step #2: Search for " + testData.getQuery());
            youTubeHomePage.search(testData.getQuery());

            logHelper.logStep("Step #3: Check for result");
            youTubeHomePage.verifyResultsVisible();
        }
    }

}
