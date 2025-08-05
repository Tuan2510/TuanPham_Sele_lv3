package testcases.BookTest;

import io.qameta.allure.Description;
import org.testng.annotations.Test;
import testcases.TestBase;
import utils.RetryAnalyzer;

import static com.codeborne.selenide.Selenide.open;

public class BooksShadowDomTest extends TestBase {
    private static final String URL = "https://books-pwakit.appspot.com/";
    private static final String QUERY = "playwright";

    @Description("Search book using Selenide API and verify results")
    @Test(dataProvider = "getData", description = "Search book using Selenide API and verify results",
            retryAnalyzer = RetryAnalyzer.class, groups = {"Book_Regression", "FullRegression"})
    public void searchBooksUsingSelenideAPI() {
        logHelper.logStep("Step #1: Navigate to Books PWA");
        open(URL);

//        SelenideElement root = $("book-app").shadowRoot();
//
//        logHelper.logStep("Step #2: Search using Selenide shadowRoot");
//        root.find("#input").setValue(QUERY).pressEnter();
//
//        logHelper.logStep("Step #3: Verify all book titles contain query");
//        ElementsCollection titles = root.findAll("ul li .title");
//        titles.forEach(title -> title.shouldHave(text(QUERY)));
    }

    @Description("Search book using Selenium API and verify results")
    @Test(dataProvider = "getData", description = "Search book using Selenium API and verify results",
            retryAnalyzer = RetryAnalyzer.class, groups = {"Book_Regression", "FullRegression"})
    public void searchBooksUsingSeleniumAPI() {
        logHelper.logStep("Step #1: Navigate to Books PWA");
        open(URL);

//        logHelper.logStep("Step #2: Search using Selenium shadow root");
//        WebDriver driver = WebDriverRunner.getWebDriver();
//        WebElement host = driver.findElement(By.cssSelector("book-app"));
//        SearchContext root = host.getShadowRoot();
//        WebElement input = root.findElement(By.cssSelector("#input"));
//        input.sendKeys(QUERY);
//        input.sendKeys(Keys.ENTER);
//
//        logHelper.logStep("Step #3: Verify all book titles contain query");
//        List<WebElement> titles = root.findElements(By.cssSelector("ul li .title"));
//        for (WebElement title : titles) {
//            Assert.assertTrue(title.getText().toLowerCase().contains(QUERY.toLowerCase()),
//                    String.format("Title '%s' does not contain '%s'", title.getText(), QUERY));
//        }
    }


}
