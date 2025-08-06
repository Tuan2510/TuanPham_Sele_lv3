package pageObjects.BooksPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import utils.ElementHelper;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;

public class BooksHomePage {
    private static final String SelenideSearchInput = "input#input";

    private static final SelenideElement rootHost = $("book-app");
    private static final List<String> SeleniumSelectorChain = Arrays.asList(
            "app-header app-toolbar.toolbar-bottom book-input-decorator input#input"
    ); // List of chain selectors to find the shadow DOM element, each item represents a shadow DOM level

    //methods
    /**
     * Searches for a book using Selenide API in the shadow DOM.
     * @param query The search query to input.
     */
    public void searchBookUsingSelenideAPI(String query) {
        SelenideElement searchInput = $(Selectors.shadowDeepCss(SelenideSearchInput));
        searchInput.setValue(query).pressEnter();
    }

    /**
     * Searches for a book using Selenium API in the shadow DOM.
     * @param query The search query to input.
     */
    public void searchBookUsingSeleniumAPI(String query) {
        rootHost.shouldBe(Condition.visible); // Ensure the root host is visible before proceeding
        SelenideElement input = $(ElementHelper.getShadowElementBySelenium(rootHost, SeleniumSelectorChain));
        if (input != null) {
            input.setValue(query).pressEnter();
        } else {
             throw new RuntimeException("Search input element not found in shadow DOM");
        }
    }

}
