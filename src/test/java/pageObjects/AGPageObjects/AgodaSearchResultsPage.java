package pageObjects.AGPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testDataObject.AGTest.Facilities;
import testDataObject.AGTest.Hotel;
import testDataObject.AGTest.PriceFilter;
import utils.LanguageManager;
import utils.LogHelper;
import utils.TestListener;

import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
import static utils.ValueHelper.parseHotelRatingFloat;
import static utils.ValueHelper.parsePrice;

public class AgodaSearchResultsPage {
    private static final Logger logger = LoggerFactory.getLogger(AgodaSearchResultsPage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    // Locators
    private final SelenideElement minPriceFilter = $("#SideBarLocationFilters #price_box_0");
    private final SelenideElement maxPriceFilter = $("#SideBarLocationFilters #price_box_1");
    private final ElementsCollection hotelCards = $$("[data-selenium='hotel-item']");
    @Getter
    private final ElementsCollection hotelImagesList = $$("[data-element-name='property-card-gallery']");

    // String locators
    private final String hotelImgCss = "[data-element-name='property-card-gallery']";
    private final String hotelNameCss = "[data-selenium='hotel-name']";
    private final String hotelAddressCss = "[data-selenium='area-city']";
    private final String hotelRatingCss = "[data-testid='rating-container'] span";
    private final String hotelPriceCss = "[data-selenium='display-price']";
    private final String hotelFinalPriceCss = "[data-element-name='final-price']";
    private final String hotelSoldOutCss = ".SoldOutMessage";
    private final String PriceFilterValueAttribute = "value";

    //Dynamic locators
    private final String starRatingFilter = "//fieldset[legend[@id='filter-menu-StarRatingWithLuxury']]//label[@data-element-index='%s']//input";
    private final String sortByOption = "//button[div/span[text()='%s']]";
    private final String filterOption = "//div[@id='SideBarLocationFilters']//span[text()='%s']";

    private final String hotelRating = ".//div[@data-element-name='property-card-review']/div/div";
    private final String hotelRatingReview = "//li[span[text()='%s']]/strong";

    //Methods
    /**
     * Verifies that the search results page is displayed by checking the URL.
     */
    public void verifyPageIsDisplayed() {
        switchTo().window(1);
        webdriver().shouldHave(urlContaining("/search?"));
        logHelper.logStep("Search results page is displayed with URL: %s", webdriver().driver().getCurrentFrameUrl());
    }

    private void setMinPriceFilter(int minPrice) {
        scrollToElement(minPriceFilter);
        minPriceFilter.click();
        minPriceFilter.clear();
        minPriceFilter.pressEnter();
        minPriceFilter.shouldBe(Condition.empty, Duration.ofSeconds(5));
        minPriceFilter.setValue(String.valueOf(minPrice)).pressEnter();
    }

    private void setMaxPriceFilter(int maxPrice) {
        scrollToElement(maxPriceFilter);
        maxPriceFilter.click();
        maxPriceFilter.clear();
        maxPriceFilter.pressEnter();
        maxPriceFilter.shouldBe(Condition.empty, Duration.ofSeconds(5));
        maxPriceFilter.setValue(String.valueOf(maxPrice)).pressEnter();
    }

    /**
     * Gets the current price filter values from the search results page.
     *
     * @return PriceFilter object containing the minimum and maximum price values.
     */
    public PriceFilter getFilterValues() {
        logHelper.logStep("Getting current price filter values");
        scrollToPageTop();
        scrollToElement(minPriceFilter);

        int minPrice = parsePrice(minPriceFilter.getAttribute(PriceFilterValueAttribute));
        int maxPrice = parsePrice(maxPriceFilter.getAttribute(PriceFilterValueAttribute));
        return new PriceFilter(minPrice, maxPrice);
    }

    /**
     * Sets the price filter for the search results.
     *
     * @param minPrice The minimum price to filter by.
     * @param maxPrice The maximum price to filter by.
     */
    public void setPriceFilter(int minPrice, int maxPrice) {
        logHelper.logStep("Setting price filter: min= %s, max= %s", formatPrice(minPrice), formatPrice(maxPrice));

        setMinPriceFilter(minPrice);
        waitForHotelCardsToLoad();

        setMaxPriceFilter(maxPrice);
        waitForHotelCardsToLoad();
    }

    private void selectStarRating(int rating) {
        SelenideElement starRatingElement = $x(String.format(starRatingFilter, rating));
        starRatingElement.scrollIntoView(true);
        starRatingElement.click();
        // Wait for the results to update
        waitForHotelCardsToLoad();
    }

    /**
     * Filters the search results by star rating.
     *
     * @param rating The star rating to filter by (1 to 5).
     */
    public void filterByStarRating(int rating) {
        logHelper.logStep("Filtering results by star rating: %s stars", rating);
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
        waitForHotelCardsToLoad();
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

            waitForHotelCardsToLoad();

            int newLoadedImages = getHotelImagesList().size();
            if (newLoadedImages == currentLoadedImages) {
                break;
            }
            currentLoadedImages = newLoadedImages;
            availableCount = getAvailableHotelCount();
        }
    }

    private void waitForHotelCardsToLoad() {
        getHotelImagesList().shouldHave(CollectionCondition.sizeGreaterThan(0), Duration.ofSeconds(10));
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

    private void waitForHotelCardToLoad(SelenideElement hotelCard) {
        hotelCard.$(hotelImgCss).shouldBe(Condition.visible, Duration.ofSeconds(10));
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
                    waitForHotelCardToLoad(element);

                    Hotel hotel = new Hotel();

                    hotel.setName(getSafeText(element, hotelNameCss));
                    hotel.setAddress(getSafeText(element, hotelAddressCss));

                    if( element.$(hotelRatingCss).isDisplayed() ) {
                        hotel.setRating(parseHotelRatingFloat(getSafeText(element, hotelRatingCss)));
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
        logHelper.logStep("Expecting %s hotels in search results with location containing: %s", numberOfHotels, expectedLocation);
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);

        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found in search results.");
        }
        //check the location of the results
        for (Hotel hotel : hotels) {
            logHelper.logStep("Checking hotel: [%s], with address: [%s]", hotel.getName(), hotel.getAddress());
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
            logHelper.logStep("Checking hotel: [%s], with price: %s", hotels.get(i).getName(), formatPrice(hotels.get(i).getPrice()));
            if (hotels.get(i).getPrice() > hotels.get(i + 1).getPrice()) {
                throw new AssertionError("Hotels are not sorted by lowest price.");
            }
        }
    }

    /**
     * Verifies that the price filter is applied correctly by comparing the expected and actual filter values.
     *
     * @param expectedFilter The expected price filter values.
     */
    public void verifyFilterApplied(PriceFilter expectedFilter) {
        logHelper.logStep("Verifying that the filter is applied");
        PriceFilter actualFilter = getActualPriceFilter();
        if (actualFilter.getPriceMin() != expectedFilter.getPriceMin() || actualFilter.getPriceMax() != expectedFilter.getPriceMax()) {
            throw new AssertionError("Price filter values do not match. Expected: " + expectedFilter + ", Actual: " + actualFilter);
        }
    }

    private PriceFilter getActualPriceFilter() {
        logHelper.logStep("Getting actual price filter values");
        int minPrice = parsePrice(minPriceFilter.scrollIntoView(true).getAttribute("value") );
        int maxPrice = parsePrice(maxPriceFilter.scrollIntoView(true).getAttribute("value") );
        return new PriceFilter(minPrice, maxPrice);
    }

    /**
     * Verifies that the hotel prices are within the specified range after applying the filter.
     *
     * @param numberOfHotels The number of hotels to check in the results.
     * @param minPrice       The minimum price to check against.
     * @param maxPrice       The maximum price to check against.
     */
    public void verifyHotelPriceAfterFilter(int numberOfHotels, int minPrice, int maxPrice) {
        logHelper.logStep("Verifying hotel prices after applying filter: %s - %s", formatPrice(minPrice), formatPrice(maxPrice));
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);

        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found after filtering.");
        }

        for (Hotel hotel : hotels) {
            logHelper.logStep("Checking hotel: [%s], with price: %s", hotel.getName(), formatPrice(hotel.getPrice()));
            if (hotel.getPrice() < minPrice || hotel.getPrice() > maxPrice) {
                throw new AssertionError("Hotel price is out of the specified range. Hotel name: " + hotel.getName() +
                        ", price: " + formatPrice(hotel.getPrice()) +
                        ", expected range: " + formatPrice(minPrice) + " - " + formatPrice(maxPrice));
            }
        }
    }

    /**
     * Verifies that the hotel star ratings are at least the specified rating after applying the filter.
     *
     * @param numberOfHotels The number of hotels to check in the results.
     * @param starRating     The minimum star rating to check against.
     */
    public void verifyHotelStarRatingAfterFilter(int numberOfHotels, int starRating) {
        logHelper.logStep("Verifying hotel star ratings after applying filter: %s stars", starRating);
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);

        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found after filtering.");
        }

        for (Hotel hotel : hotels) {
            logHelper.logStep("Checking hotel: [%s], with rating: %.1f stars", hotel.getName(), hotel.getRating());
            if (hotel.getRating() < starRating) {
                throw new AssertionError("Hotel rating is below the specified star rating. Hotel name: " + hotel.getName() +
                        ", rating: " + hotel.getRating() +
                        ", expected minimum rating: " + starRating);
            }
        }
    }

    /**
     * Resets the price filter by set the default values.
     */
    public void resetPriceFilter(PriceFilter defaultPriceFilter) {
        logHelper.logStep("Resetting price filter by enter default values");
        setPriceFilter(defaultPriceFilter.getPriceMin(), defaultPriceFilter.getPriceMax());
        waitForHotelCardsToLoad();
    }

    /**
     * Verifies that the price filter is reset by checking the input fields.
     */
    public void verifyPriceFilterReset(PriceFilter defaultPriceFilter) {
        logHelper.logStep("Verifying that the price filter is reset");

        // Get the actual displayed price filter values
        PriceFilter displayedPriceValues = getActualPriceFilter();

        // Check if the displayed prices should reset to the expected filter values
        if (displayedPriceValues.getPriceMin() == defaultPriceFilter.getPriceMin()|| displayedPriceValues.getPriceMax() == defaultPriceFilter.getPriceMax()) {
            logHelper.logStep("Price filter display values are reset correctly: " +
                    "Min: " + formatPrice(displayedPriceValues.getPriceMin()) + "; Max: " + formatPrice(displayedPriceValues.getPriceMax()));
        } else {
            throw new AssertionError("Display price filter is not reset correctly. Current display price: " +
                    "Min: " + formatPrice(displayedPriceValues.getPriceMin()) + "; Max: " + formatPrice(displayedPriceValues.getPriceMax())
                    + ". Expected: " + "Min: " + formatPrice(defaultPriceFilter.getPriceMin()) + "; Max: " + formatPrice(defaultPriceFilter.getPriceMax()));
        }

    }

    /**
     * Filters the hotel results by the swimming pool facility.
     */
    public void filterByFacilities(Facilities facility) {
        logHelper.logStep("Filtering results by swimming pool facility");
        SelenideElement targetFilter = $x(String.format(filterOption, facility.getFacility()));
        targetFilter.scrollIntoView(true).shouldBe(Condition.visible).click();
        scrollToPageTop();
        waitForHotelCardsToLoad();
    }

    /**
     * Opens the hotel details page of the hotel at the given index.
     *
     * @param index index of the hotel (1-based)
     * @return Hotel object containing basic information of the selected hotel
     */
    public Hotel openHotelDetailsByIndex(int index) {
        loadHotelResults(index);
        List<SelenideElement> loadedCards = getLoadedHotelCards();
        if (index <= 0 || index > loadedCards.size()) {
            throw new IllegalArgumentException("Hotel index is out of bounds");
        }
        SelenideElement card = loadedCards.get(index - 1);
        card.scrollIntoView(true);
        Hotel hotel = new Hotel();
        hotel.setName(getSafeText(card, hotelNameCss));
        hotel.setAddress(getSafeText(card, hotelAddressCss));
        card.click();
        switchTo().window(2);
        return hotel;
    }

    /**
     * Retrieves review scores for a hotel by hovering on its rating element.
     *
     * @param index index of the hotel (1-based)
     * @param categories list of review categories to capture
     * @return map of review category to its score text
     */
    public Map<String, String> getHotelReviewScores(int index, List<String> categories) {
        loadHotelResults(index);
        List<SelenideElement> loadedCards = getLoadedHotelCards();
        if (index <= 0 || index > loadedCards.size()) {
            throw new IllegalArgumentException("Hotel index is out of bounds");
        }
        SelenideElement card = loadedCards.get(index - 1);
        SelenideElement hotelRatingScore = card.$x(hotelRating);
        scrollToElement(hotelRatingScore);
        hotelRatingScore.hover();
        logHelper.logStep("Retrieving review scores for hotel [%s]", getSafeText(card, hotelNameCss));

        Map<String, String> scores = new HashMap<>();
        for (String category : categories) {
            SelenideElement scoreElement = $x(String.format(hotelRatingReview, category));
            String value = scoreElement.shouldBe(Condition.visible, Duration.ofSeconds(5)).getText();
            scores.put(category, value);
            logHelper.logStep("Retrieved review scores: %s - %s", category, scores);
        }

        return scores;
    }

}
