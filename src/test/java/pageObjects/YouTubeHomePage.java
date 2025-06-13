package pageObjects;

import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Condition.appear;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.open;


public class YouTubeHomePage {
    // Locators
    private final SelenideElement searchInput = $("input[name='search_query']");
    private final SelenideElement searchResults = $("#contents");

    public void openHome() {
        open("https://www.youtube.com");
    }

    public void search(String query) {
        searchInput.setValue(query).pressEnter();
    }

    public void verifyResultsVisible() {
        searchResults.should(appear);
    }



}