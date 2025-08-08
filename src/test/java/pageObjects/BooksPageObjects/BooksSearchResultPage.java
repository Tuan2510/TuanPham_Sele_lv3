package pageObjects.BooksPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testDataObject.ShadowStep;
import utils.ElementHelper;
import utils.LogHelper;
import utils.TestListener;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selectors.shadowDeepCss;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static commons.Constants.DEFAULT_SCROLL_STEPS;
import static utils.ElementHelper.scrollToBottomWithSteps;
import static utils.ElementHelper.scrollToPageTop;

public class BooksSearchResultPage {
    private static final Logger logger = LoggerFactory.getLogger(BooksSearchResultPage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    private static final String loadedBookItem = "ul.books li div.placeholder";
    private static final String SelenideBookTitleQuery = ".title-container";

    private static final SelenideElement rootHost = $("book-app");
    private static final List<ShadowStep> SeleniumShadowStepChain = Arrays.asList(
            new ShadowStep("book-explore", false),
            new ShadowStep("book-item", true),
            new ShadowStep(".title-container", false)
    ); // List of chain selectors to find the shadow DOM element, each item represents a shadow DOM level

    // Methods
    private void waitBookItemsToLoad() {
        scrollToBottomWithSteps(DEFAULT_SCROLL_STEPS);
        $$(shadowDeepCss(loadedBookItem)).shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
        scrollToPageTop();
    }

    private ElementsCollection getBookTitlesUsingSelenideAPI() {
        waitBookItemsToLoad();
        // Ensure the root host is visible before proceeding
        rootHost.shouldBe(com.codeborne.selenide.Condition.visible, Duration.ofSeconds(10));
        return $$(shadowDeepCss(SelenideBookTitleQuery));
    }

    private List<WebElement> getBookTitlesUsingSeleniumAPI() {
        waitBookItemsToLoad();
        // Ensure the root host is visible before proceeding
        rootHost.shouldBe(com.codeborne.selenide.Condition.visible, Duration.ofSeconds(10));
        List<WebElement> titles = ElementHelper.getAllShadowElementsBySelenium(rootHost, SeleniumShadowStepChain);
        if (titles == null || titles.isEmpty()) {
            throw new AssertionError("Book titles not found in shadow DOM using Selenium API");
        }
        return titles;
    }

    /**
     * Verifies that all book titles found on the search result page using the Selenide API.
     * Logs the number of book titles found and each title being verified.
     * Throws an AssertionError if no titles are found or if any title does not contain the query.
     *
     @param query the string that each book title should contain (case-insensitive)
     */
    public void verifyBookTitlesWithSelenideAPI(String query) {
        ElementsCollection titles = getBookTitlesUsingSelenideAPI();
        titles.shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));

        logHelper.logStep("Number of book titles found: %s", titles.size());

        if (!titles.stream()
                .map(SelenideElement::getText)
                .peek(text -> logHelper.logStep(String.format("Verifying title: '%s'", text)))
                .allMatch(text -> text.toLowerCase()
                        .contains(query.toLowerCase())) ) {
            throw new AssertionError("Not all book titles contain: " + query);
        }
    }

    /**
     * Verifies that all book titles found on the search result page using the Selenium API.
     * Logs the number of book titles found and each title being verified.
     * Throws an AssertionError if no titles are found or if any title does not contain the query.
     *
     * @param query the string that each book title should contain (case-insensitive)
     */
    public void verifyBookTitlesWithSeleniumAPI(String query) {
        List<WebElement> titles = getBookTitlesUsingSeleniumAPI();
        if (titles.isEmpty()) {
            throw new AssertionError("Book titles not found in shadow DOM using Selenium API");
        }

        logHelper.logStep("Number of book titles found: %s", titles.size());

        if (!titles.stream()
                .map(WebElement::getText)
                .peek(text -> logHelper.logStep(String.format("Verifying title: '%s'", text)))
                .allMatch(text -> text.toLowerCase()
                        .contains(query.toLowerCase())) ) {
            throw new AssertionError("Not all book titles contain: " + query);
        }
    }

}
