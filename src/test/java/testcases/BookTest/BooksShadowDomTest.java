package testcases.BookTest;

import io.qameta.allure.Description;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import pageObjects.BooksPageObjects.BooksHomePage;
import pageObjects.BooksPageObjects.BooksSearchResultPage;
import testcases.TestBase;
import utils.RetryAnalyzer;
import utils.TestListener;

import static driver.DriverFactory.openHomePage;

@Listeners({TestListener.class})
public class BooksShadowDomTest extends TestBase {
    private static final String QUERY = "playwright";
    BooksHomePage booksHomePage = new BooksHomePage();
    BooksSearchResultPage booksSearchResultPage = new BooksSearchResultPage();

    @Description("Search book using Selenide API and verify results")
    @Test(description = "Search book using Selenide API and verify results",
            retryAnalyzer = RetryAnalyzer.class, groups = {"Book_Regression", "FullRegression"})
    public void searchBooksUsingSelenideAPI() {
        logHelper.logStep("Step #1: Navigate to Books PWA");
        openHomePage();

        logHelper.logStep("Step #2: Search using Selenide shadowRoot");
        booksHomePage.searchBookUsingSelenideAPI(QUERY);

        logHelper.logStep("Step #3: Verify all book titles contain query");
        booksSearchResultPage.verifyBookTitlesWithSelenideAPI(QUERY);
    }

    @Description("Search book using Selenium API and verify results")
    @Test(description = "Search book using Selenium API and verify results",
            retryAnalyzer = RetryAnalyzer.class, groups = {"Book_Regression", "FullRegression"})
    public void searchBooksUsingSeleniumAPI() {
        logHelper.logStep("Step #1: Navigate to Books PWA");
        openHomePage();

        logHelper.logStep("Step #2: Search using Selenide shadowRoot");
        booksHomePage.searchBookUsingSeleniumAPI(QUERY);

        logHelper.logStep("Step #3: Verify all book titles contain query");
        booksSearchResultPage.verifyBookTitlesWithSeleniumAPI(QUERY);
    }


}
