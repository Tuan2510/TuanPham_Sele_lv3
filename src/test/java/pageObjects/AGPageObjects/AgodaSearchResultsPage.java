package pageObjects.AGPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import testDataObject.AGTest.Hotel;
import utils.LanguageManager;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.switchTo;
import static com.codeborne.selenide.Selenide.webdriver;
import static com.codeborne.selenide.WebDriverConditions.urlContaining;
import static utils.ElementHelper.scrollToElement;
import static utils.ElementHelper.scrollToPageTop;
import static utils.ValueHelper.getSafeText;
import static utils.ValueHelper.parseFloatSafe;
import static utils.ValueHelper.parsePrice;

public class AgodaSearchResultsPage {
    // Locators
    private final SelenideElement minPriceFilter = $("#SideBarLocationFilters #price_box_0");
    private final SelenideElement maxPriceFilter = $("#SideBarLocationFilters #price_box_1");
    private final SelenideElement hotelContainer = $("#contentContainer");
    private final ElementsCollection hotelCards = $$("[data-selenium='hotel-item']");
    @Getter
    private final ElementsCollection hotelImagesList = $$("[data-element-name='property-card-gallery']");

    // String locators
    private final String hotelNameCss = "[data-selenium='hotel-name']";
    private final String hotelAddressCss = "[data-selenium='area-city']";
    private final String hotelRatingCss = "[data-testid='rating-container'] span";
    private final String hotelPriceCss = "[data-selenium='display-price']";
    private final String hotelFinalPriceCss = "[data-element-name='final-price']";
    private final String hotelSoldOutCss = ".SoldOutMessage";

    //Dynamic locators
    private final String starRatingFilter = "//fieldset[legend[@id='filter-menu-StarRatingWithLuxury']]//label[@data-element-index='%s']//input";
    private final String sortByOption = "//button[div/span[text()='%s']]";

    //Methods
    /**
     * Verifies that the search results page is displayed by checking the URL.
     */
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
        SelenideElement sortByElement = $x(String.format(sortByOption, sortBy));
        scrollToPageTop();
        sortByElement.shouldBe(Condition.visible).click();
    }

    @Step("Sort results by: {sortBy}")
    public void sortByLowestPrice() {
        selectSortBy(LanguageManager.get("lowest_price_first"));
    }

    private void loadHotelResults(int numberOfHotels) {
        int currentCount = getHotelImagesList().size();

        while (currentCount < numberOfHotels*2) {
            SelenideElement lastCard = getHotelImagesList().last();
            lastCard.scrollIntoView(true);

            // Wait until more hotel cards are loaded
            getHotelImagesList().shouldHave(CollectionCondition.sizeGreaterThan(currentCount), Duration.ofSeconds(10));

            int newCount = getHotelImagesList().size();
            if (newCount >= currentCount) {
                // reached the number of hotels we want so we can break the loop
                break;
            }
            currentCount = newCount;
        }
    }

    private List<Hotel> getHotelsFromResults(int numberOfHotels) {
        loadHotelResults(numberOfHotels);

        int limit = Math.min(hotelCards.size(), numberOfHotels);

        //need to handle the case that the hotel is sold out
        return hotelCards.stream()
                .filter(element -> !element.$(".SoldOutMessage").exists())
                .limit(limit)
                .map(element -> {
                    Hotel hotel = new Hotel();

                    hotel.setName(getSafeText(element, hotelNameCss));
                    hotel.setAddress(getSafeText(element, hotelAddressCss));
                    hotel.setRating(parseFloatSafe(getSafeText(element, hotelRatingCss)));

                    if( element.$(hotelFinalPriceCss).isDisplayed() ) {
                        hotel.setPrice(parsePrice(getSafeText(element, hotelFinalPriceCss)));
                    } else {
                        hotel.setPrice(parsePrice(getSafeText(element, hotelPriceCss)));
                    }

                    return hotel;
                })
                .toList();
    }

    public void verifySearchResultsHotelAddress(int numberOfHotels, String expectedLocation) {
        switchTo().window(1);
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);

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
