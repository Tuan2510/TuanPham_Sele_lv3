package pageObjects.BooksPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import utils.ElementHelper;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;

public class BooksHomePage {


    private static final String SelenideSearchInput =
            "book-app app-header app-toolbar.toolbar-bottom book-input-decorator input#input";

    private static final SelenideElement rootHost = $("book-app");
    private static final String SeleniumSearchInput =
            "app-header>>>app-toolbar.toolbar-bottom>>>book-input-decorator>>>input#input";

    //methods
    public void searchBookUsingSelenideAPI(String query) {
        SelenideElement searchInput = $(Selectors.shadowDeepCss(SelenideSearchInput));
        searchInput.setValue(query).pressEnter();
    }

    public void searchBookUsingSeleniumAPI(String query) {
        rootHost.shouldBe(Condition.exist, Condition.visible);
        SelenideElement input = ElementHelper.getShadowElementBySelenium(rootHost, SeleniumSearchInput);
        if (input != null) {
            input.setValue(query).pressEnter();
        } else {
             throw new RuntimeException("Search input element not found in shadow DOM");
        }
    }

}
