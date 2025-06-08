package testcases.youtubetest;

import base.TestBase;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import utils.retryStrategy.RetryAnalyzer;
import utils.dataProvider.TestDataProvider;
import utils.testListener.TestListener;

import java.util.Hashtable;

import static com.codeborne.selenide.Selenide.open;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Condition.appear;

@Listeners({TestListener.class})
public class YoutubeTest extends TestBase {

    @Test(dataProvider = "getData", dataProviderClass = TestDataProvider.class, retryAnalyzer = RetryAnalyzer.class)
    public void TC01(Hashtable<String, String> data) {
        System.out.println("[INFO] RunType: " + data.get("RunType"));
        open("https://www.youtube.com");
        $("input[name=search_query]").setValue(data.get("query")).pressEnter();
        System.out.println("[INFO] Key Sent: " + data.get("query"));
        $("#contents").should(appear);
    }

}
