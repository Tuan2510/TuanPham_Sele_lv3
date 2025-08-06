package pageObjects.BooksPageObjects;

import com.codeborne.selenide.Selectors;
import com.codeborne.selenide.SelenideElement;
import driver.DriverFactory;
import org.openqa.selenium.WebDriver;
import utils.ElementHelper;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static commons.Constants.BOOKS_PWA_URL;

public class BooksHomePage {
//    protected final WebDriver driver = DriverFactory.getDriver();


    private static final SelenideElement searchInput = $(Selectors.shadowDeepCss(
            "book-app app-header app-toolbar.toolbar-bottom book-input-decorator input#input"
    ));

    SelenideElement rootHost = $("book-app");
    private static final String searchInputFull =
            "app-header>>>app-toolbar.toolbar-bottom>>>book-input-decorator>>>input#input";

    //methods
    public void openHomePage() {
        open(BOOKS_PWA_URL);
    }

    public void searchBookUsingSelenideAPI(String query) {
        searchInput.setValue(query).pressEnter();
    }

    public void searchBookUsingSeleniumAPI(String query) {
        SelenideElement input = ElementHelper.getShadowElementBySelenium(rootHost, searchInputFull);
        if (input != null) {
            input.setValue(query).pressEnter();
        } else {
            throw new RuntimeException("Search input element not found in shadow DOM");
        }
    }

}
