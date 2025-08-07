package pageObjects.BooksPageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import org.openqa.selenium.WebElement;
import utils.ElementHelper;

import java.util.Arrays;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;

public class BooksSearchResultPage {
    private static final String SelenideBookTitleQuery = "div.title-container";

    private static final SelenideElement rootHost = $("book-explore");
    private static final List<String> SeleniumBookTitleChain = Arrays.asList(
            "book-item",
            "div.title-container"
    );


    private ElementsCollection getBookTitlesUsingSelenideAPI() {
        return rootHost.$$(SelenideBookTitleQuery);
    }

    private List<WebElement> getBookTitlesUsingSeleniumAPI() {
        //TODO
        return null;
    }

    public void verifyBookTitlesWithSelenideAPI(String query) {
        ElementsCollection titles = getBookTitlesUsingSelenideAPI();
        titles.forEach(title -> title.shouldHave(com.codeborne.selenide.Condition.text(query)));
    }

    public void verifyBookTitlesWithSeleniumAPI(String query) {
        List<WebElement> titles = getBookTitlesUsingSeleniumAPI();
        for (WebElement title : titles) {
            if (!title.getText().toLowerCase().contains(query.toLowerCase())) {
                throw new AssertionError(String.format("Title '%s' does not contain '%s'", title.getText(), query));
            }
        }
    }

}
