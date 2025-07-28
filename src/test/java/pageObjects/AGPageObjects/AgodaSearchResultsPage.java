package pageObjects.AGPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import testDataObject.AGTest.Hotel;
import utils.LanguageManager;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.ElementHelper.scrollToElement;
import static utils.ValueHelper.getSafeText;
import static utils.ValueHelper.parseFloatSafe;
import static utils.ValueHelper.parsePrice;

public class AgodaSearchResultsPage {
    private final SelenideElement minPriceFilter = $("#SideBarLocationFilters #price_box_0");
    private final SelenideElement maxPriceFilter = $("#SideBarLocationFilters #price_box_1");
    private final SelenideElement hotelContainer = $("#contentContainer");
    private final ElementsCollection hotelCards = $$("[data-selenium='hotel-item']");


    //Dynamic locators
    private final String starRatingFilter = "//fieldset[legend[@id='filter-menu-StarRatingWithLuxury']]//label[@data-element-index='%s']//input";
    private final String sortByOption = "//button[div/span[text()='%s']]";

    //Methods
    public void verifyPageIsDisplayed() {
        webdriver().shouldHave(urlContaining("/search?"));
    }

    private void setMinPriceFilter(String minPrice) {
        scrollToElement(minPriceFilter);
        minPriceFilter.setValue(minPrice);
    }

    private void setMaxPriceFilter(String maxPrice) {
        scrollToElement(maxPriceFilter);
        maxPriceFilter.setValue(maxPrice);
    }

    @Step("Set price filter with min: {minPrice} and max: {maxPrice}")
    public void setPriceFilter(String minPrice, String maxPrice) {
        setMinPriceFilter(minPrice);
        setMaxPriceFilter(maxPrice);
    }

    private void selectStarRating(int rating) {
        SelenideElement starRatingElement = $(String.format(starRatingFilter, rating));
        scrollToElement(starRatingElement);
        starRatingElement.click();
    }

    @Step("Filter by rating: {rating} stars")
    public void filterByStarRating(int rating) {
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        selectStarRating(rating);
    }

    private void selectSortBy(String sortBy) {
        SelenideElement sortByElement = $(String.format(sortByOption, sortBy));
        scrollToElement(sortByElement);
        sortByElement.click();
    }

    @Step("Sort results by: {sortBy}")
    public void sortByLowestPrice() {
        selectSortBy(LanguageManager.get("lowest_price_first"));
    }

    private void waitForHotelCardsToLoad() {
        $("#contentContainer").shouldBe(Condition.visible, Duration.ofSeconds(10));
        $$("[data-selenium='hotel-item']").shouldBe(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
    }

    private void loadHotelResults(int numberOfHotels) {
        int currentCount = $$("[data-selenium='hotel-item']").size();

        while (currentCount < numberOfHotels) {
            SelenideElement lastCard = $$("[data-selenium='hotel-item']").last();
            scrollToElement(lastCard);

            // Wait until more hotel cards are loaded
            $$("[data-selenium='hotel-item']").shouldHave(CollectionCondition.sizeGreaterThan(currentCount), Duration.ofSeconds(10));

            int newCount = $$("[data-selenium='hotel-item']").size();
            if (newCount == currentCount) {
                // No more hotels were loaded, possibly end of list
                break;
            }
            currentCount = newCount;
        }
    }

    private List<Hotel> getHotelsFromResults(int numberOfHotels) {
        waitForHotelCardsToLoad();
        loadHotelResults(numberOfHotels);

        ElementsCollection hotelElements = $$(".PropertyCardItem");
        int limit = Math.min(hotelElements.size(), numberOfHotels);

        return hotelElements.stream()
                .limit(limit)
                .map(element -> {
                    Hotel hotel = new Hotel();

                    hotel.setName(getSafeText(element, "[data-selenium='hotel-name']"));
                    hotel.setAddress(getSafeText(element, "[data-selenium='area-city']"));
                    hotel.setRating(parseFloatSafe(getSafeText(element, "[data-testid='rating-container'] span")));
                    hotel.setPrice(parsePrice(getSafeText(element, "[data-element-name='final-price']")));

                    return hotel;
                })
                .toList();
    }

    //---
    public List<Hotel> getHotels(int count) {
        String cardContainer = "PropertyCardItem";
        List<Hotel> cardContainers = new ArrayList<>();

        int retries = 0;
        // scroll into center of each card
        // for card loaded lazily, wait for it and its attribute to be displayed
        while (cardContainers.size() < count && retries++ < 2 * count) {
            int currIndex = cardContainers.size();
            SelenideElement card = $$(By.className(cardContainer)).get(currIndex).$(":first-child");

            card.scrollIntoView("{block: 'center'}").shouldBe(visible);

            Hotel hotel = getHotel(card);
            cardContainers.add(hotel);
        }

        return cardContainers;
    }

    private Hotel getHotel(SelenideElement container) {
        String dest = container.$("[data-selenium='area-city']").shouldBe(visible).getText().split("-")[0].trim();
        String name = container.$("[data-selenium='hotel-name']").shouldBe(visible).getText().trim();
        String priceText = container.$("[data-element-name='final-price']").shouldBe(visible).getText().replaceAll("\\D", "");
        float rating = Float.parseFloat(container.$("[data-testid='rating-container'] span").shouldBe(visible).getText().split(" ")[0]);
        return new Hotel(name, dest, rating, Integer.parseInt(priceText));
    }

    public void verifySearchResults(int numberOfHotels, String expectedLocation) {
//        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);
        List<Hotel> hotels = getHotels(numberOfHotels);


        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found in search results.");
        }
        //check the location of the results
        for (Hotel hotel : hotels) {
            if (!hotel.getAddress().toLowerCase().contains(expectedLocation.toLowerCase())) {
                throw new AssertionError("Hotel address does not match expected location: " + hotel.getAddress());
            }
        }
    }

    public void verifyResultsSortedByLowestPrice(int numberOfHotels) {
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);
        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found to verify sorting.");
        }

        for (int i = 0; i < hotels.size() - 1; i++) {
            if (hotels.get(i).getPrice() > hotels.get(i + 1).getPrice()) {
                throw new AssertionError("Hotels are not sorted by lowest price.");
            }
        }
    }
}
