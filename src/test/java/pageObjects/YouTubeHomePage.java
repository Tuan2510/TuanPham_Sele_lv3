package pageObjects;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;


public class YouTubeHomePage {
    // Locators
    private final SelenideElement searchInput = $("input[name='search_query']");
    private final SelenideElement searchResults = $("#contents");

    public void search(String query) {
        searchInput.setValue(query).pressEnter();
    }

    public void verifyResultsVisible() {
        searchResults.should(appear);
    }

}