package pageObjects.AGPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import io.qameta.allure.Step;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testDataObject.AGTest.Hotel;
import utils.LanguageManager;
import utils.LogHelper;
import utils.TestListener;

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
import static utils.ValueHelper.formatPrice;
import static utils.ValueHelper.getSafeText;
import static utils.ValueHelper.parseFloatSafe;
import static utils.ValueHelper.parsePrice;

public class AgodaSearchResultsPage {
    private static final Logger logger = LoggerFactory.getLogger(AgodaSearchResultsPage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

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
        switchTo().window(1);
        webdriver().shouldHave(urlContaining("/search?"));
        logHelper.logStep("Search results page is displayed with URL: " + webdriver().driver().getCurrentFrameUrl());
    }

    private void setMinPriceFilter(int minPrice) {
        scrollToElement(minPriceFilter);
        minPriceFilter.setValue(String.valueOf(minPrice));
    }

    private void setMaxPriceFilter(int maxPrice) {
        scrollToElement(maxPriceFilter);
        maxPriceFilter.setValue(String.valueOf(maxPrice));
    }

    /**
     * Sets the price filter for the search results.
     *
     * @param minPrice The minimum price to filter by.
     * @param maxPrice The maximum price to filter by.
     */
    public void setPriceFilter(int minPrice, int maxPrice) {
        logHelper.logStep("Setting price filter: " + formatPrice(minPrice) + " - " + formatPrice(maxPrice) + " VND");
        setMinPriceFilter(minPrice);
        setMaxPriceFilter(maxPrice);
        // wait for the results to update
        getHotelImagesList().shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
    }

    private void selectStarRating(int rating) {
        SelenideElement starRatingElement = $(String.format(starRatingFilter, rating));
        scrollToElement(starRatingElement);
        starRatingElement.click();
        // Wait for the results to update
        getHotelImagesList().shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
    }

    /**
     * Filters the search results by star rating.
     *
     * @param rating The star rating to filter by (1 to 5).
     */
    public void filterByStarRating(int rating) {
        logHelper.logStep("Filtering results by star rating: " + rating + " stars");
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        selectStarRating(rating);
    }

    private void selectSortBy(String sortBy) {
        SelenideElement sortByElement = $x(String.format(sortByOption, sortBy));
        scrollToPageTop();
        sortByElement.shouldBe(Condition.visible).click();
        // Wait for the sorting to apply
        getHotelImagesList().shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
    }

    /**
     * Sorts the search results by the lowest price.
     */
    public void sortByLowestPrice() {
        logHelper.logStep("Sorting results by lowest price");
        selectSortBy(LanguageManager.get("lowest_price_first"));
    }

    private void loadHotelResults(int numberOfHotels) {
        int currentLoadedImages = getHotelImagesList().size();
        int availableCount = getAvailableHotelCount();

        while (availableCount < numberOfHotels) {
            SelenideElement lastLoaded = getHotelImagesList().last();
            lastLoaded.scrollIntoView(true);

            getHotelImagesList().shouldHave(CollectionCondition.sizeGreaterThan(currentLoadedImages), Duration.ofSeconds(10));

            int newLoadedImages = getHotelImagesList().size();
            if (newLoadedImages == currentLoadedImages) {
                break;
            }
            currentLoadedImages = newLoadedImages;
            availableCount = getAvailableHotelCount();
        }
    }

    private int getAvailableHotelCount() {
        int loaded = getHotelImagesList().size();
        int count = 0;
        for (int i = 0; i < loaded && i < hotelCards.size(); i++) {
            if (!hotelCards.get(i).$(hotelSoldOutCss).exists()) {
                count++;
            }
        }
        return count;
    }

    private List<SelenideElement> getLoadedHotelCards() {
        int loadedCount = Math.min(getHotelImagesList().size(), hotelCards.size());
        List<SelenideElement> loaded = new java.util.ArrayList<>(loadedCount);
        for (int i = 0; i < loadedCount; i++) {
            loaded.add(hotelCards.get(i));
        }
        return loaded;
    }

    private List<Hotel> getHotelsFromResults(int numberOfHotels) {
        loadHotelResults(numberOfHotels);

        List<SelenideElement> loadedCards = getLoadedHotelCards();

        List<SelenideElement> availableHotelCards = loadedCards.stream()
                .filter(element -> !element.$(hotelSoldOutCss).exists())
                .toList();

        int limit = Math.min(availableHotelCards.size(), numberOfHotels);

        return availableHotelCards.stream()
                .limit(limit)
                .map(element -> {
                    scrollToElement(element);

                    Hotel hotel = new Hotel();

                    hotel.setName(getSafeText(element, hotelNameCss));
                    hotel.setAddress(getSafeText(element, hotelAddressCss));

                    if( element.$(hotelRatingCss).isDisplayed() ) {
                        hotel.setRating(parseFloatSafe(getSafeText(element, hotelRatingCss)));
                    } else {
                        hotel.setRating(0f); // Default rating if not displayed
                    }

                    if( element.$(hotelFinalPriceCss).isDisplayed() ) {
                        hotel.setPrice(parsePrice(getSafeText(element, hotelFinalPriceCss)));
                    } else {
                        hotel.setPrice(parsePrice(getSafeText(element, hotelPriceCss)));
                    }

                    return hotel;
                })
                .toList();
    }

    /**
     * Verifies that the search results contain hotels with the expected address.
     *
     * @param numberOfHotels The number of hotels to check in the results.
     * @param expectedLocation The expected location substring to match in hotel addresses.
     */
    public void verifySearchResultsHotelAddress(int numberOfHotels, String expectedLocation) {
        logHelper.logStep("Expecting "+ numberOfHotels + " hotels in location: " + expectedLocation);
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);

        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found in search results.");
        }
        //check the location of the results
        for (Hotel hotel : hotels) {
            logHelper.logStep("Checking hotel: " + hotel.getName() + ", at address: " + hotel.getAddress());
            if (!hotel.getAddress().toLowerCase().contains(expectedLocation.toLowerCase())) {
                throw new AssertionError("Hotel address does not match expected location: " + hotel.getAddress());
            }
        }
    }

    /**
     * Verifies that the search results are sorted by the lowest price.
     *
     * @param numberOfHotels The number of hotels to check in the results.
     */
    public void verifyResultsSortedByLowestPrice(int numberOfHotels) {
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);
        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found to verify sorting.");
        }

        for (int i = 0; i < hotels.size() - 1; i++) {
            logHelper.logStep("Checking hotel: " + hotels.get(i).getName() + ", with price: " + formatPrice(hotels.get(i).getPrice()) + " VND");
            if (hotels.get(i).getPrice() > hotels.get(i + 1).getPrice()) {
                throw new AssertionError("Hotels are not sorted by lowest price.");
            }
        }
    }

    /**
     * Verifies that the filter is applied by checking the URL.
     */
    public void verifyFilterApplied() {
        logHelper.logStep("Verifying that the filter is applied by ...");
        //TODO: Implement verification logic for filter application

    }

    public void verifyHotelPriceAfterFilter(int numberOfHotels, int minPrice, int maxPrice) {
        logHelper.logStep("Verifying hotel prices after applying filter: " + formatPrice(minPrice) + " - " + formatPrice(maxPrice) + " VND");
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);

        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found after filtering.");
        }

        for (Hotel hotel : hotels) {
            logHelper.logStep("Checking hotel: " + hotel.getName() + ", with price: " + formatPrice(hotel.getPrice()) + " VND");
            if (hotel.getPrice() < minPrice || hotel.getPrice() > maxPrice) {
                throw new AssertionError("Hotel price is out of the specified range: " + hotel.getPrice());
            }
        }
    }

    public void verifyHotelStarRatingAfterFilter(int numberOfHotels, int starRating) {
        logHelper.logStep("Verifying hotel star ratings after applying filter: " + starRating + " stars");
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);

        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found after filtering.");
        }

        for (Hotel hotel : hotels) {
            logHelper.logStep("Checking hotel: " + hotel.getName() + ", with rating: " + hotel.getRating() + " stars");
            if (hotel.getRating() < starRating) {
                throw new AssertionError("Hotel rating is below the specified star rating: " + hotel.getRating());
            }
        }
    }

    /**
     * Resets the price filter by clearing the input fields.
     */
    public void resetPriceFilter() {
        logHelper.logStep("Resetting price filter");
        //TODO: Implement reset logic for price filter
    }

    /**
     * Verifies that the price filter is reset by checking the input fields.
     */
    public void verifyPriceFilterReset() {
        logHelper.logStep("Verifying that the price filter is reset");
        //TODO: Implement verification logic for price filter reset
    }
}
