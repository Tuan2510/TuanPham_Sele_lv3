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

@Listeners({TestListener.class})
public class YoutubeTest extends TestBase {
    private static final Logger logger = LoggerFactory.getLogger(YoutubeTest.class);
    YouTubeHomePage youTubeHomePage = new YouTubeHomePage();

    @Test(dataProvider = "getData", dataProviderClass = TestDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
    public void TC01(Hashtable<String, String> data) {
        logger.info("[INFO] RunType: {}", data.get("RunType"));

        youTubeHomePage.openHome();
        youTubeHomePage.search(data.get("query"));

        logger.info("[INFO] Key Sent: {}", data.get("query"));
        youTubeHomePage.verifyResultsVisible();
    }

}
