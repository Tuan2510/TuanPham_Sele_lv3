package testcases.SampleYTTest;

import base.TestBase;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.YouTubeHomePage;
import utils.RetryAnalyzer;
import utils.TestDataProvider;
import utils.TestListener;

import java.util.Hashtable;
import java.util.Map;

@Listeners({TestListener.class})
public class YoutubeTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(YoutubeTest.class);
    YouTubeHomePage youTubeHomePage = new YouTubeHomePage();

    @Test(dataProvider = "getData", dataProviderClass = TestDataProvider.class, retryAnalyzer = RetryAnalyzer.class, groups = "Regression")
    public void TC01(Map<String, String> data) {
        logHelper.logStep("Step #1: Navigate to YouTube");
        youTubeHomePage.openHome();

        logHelper.logStep("Step #2: Search for " + data.get("query"));
        youTubeHomePage.search(data.get("query"));

        logHelper.logStep("Step #3: Check for result");
        youTubeHomePage.verifyResultsVisible();
    }

}
