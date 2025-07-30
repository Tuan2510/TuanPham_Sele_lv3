package pageObjects.AGPageObjects;

import com.codeborne.selenide.CollectionCondition;
import com.codeborne.selenide.Condition;
import com.codeborne.selenide.ElementsCollection;
import com.codeborne.selenide.SelenideElement;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testDataObject.AGTest.Hotel;
import testDataObject.AGTest.PriceFilter;
import utils.LanguageManager;
import utils.LogHelper;
import utils.TestListener;

import java.time.Duration;
import java.util.List;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.executeJavaScript;
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
    private final SelenideElement priceFilterSliderMin = $(".rc-slider-handle-1");
    private final SelenideElement priceFilterSliderMax = $(".rc-slider-handle-2");

    // String locators
    private final String hotelNameCss = "[data-selenium='hotel-name']";
    private final String hotelAddressCss = "[data-selenium='area-city']";
    private final String hotelRatingCss = "[data-testid='rating-container'] span";
    private final String hotelPriceCss = "[data-selenium='display-price']";
    private final String hotelFinalPriceCss = "[data-element-name='final-price']";
    private final String hotelSoldOutCss = ".SoldOutMessage";
    private final String priceMinValueAttribute = "aria-valuemin";
    private final String priceMaxValueAttribute = "aria-valuemax";
    private final String priceNowValueAttribute = "aria-valuenow";
    private final String priceDisplayValueAttribute = "aria-valuetext";

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
     * Verifies that the price filter is applied correctly by comparing the expected and actual filter values.
     *
     * @param expectedFilter The expected price filter values.
     */
    public void verifyFilterApplied(PriceFilter expectedFilter) {
        logHelper.logStep("Verifying that the filter is applied by ...");
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

    /**
     * Verifies that the hotel star ratings are at least the specified rating after applying the filter.
     *
     * @param numberOfHotels The number of hotels to check in the results.
     * @param starRating     The minimum star rating to check against.
     */
    public void verifyHotelStarRatingAfterFilter(int numberOfHotels, int starRating) {
        logHelper.logStep("Verifying hotel star ratings after applying filter: " + starRating + " stars");
        List<Hotel> hotels = getHotelsFromResults(numberOfHotels);

        if (hotels.isEmpty()) {
            throw new AssertionError("No hotels found after filtering.");
        }

        for (Hotel hotel : hotels) {
            logHelper.logStep("Checking hotel: " + hotel.getName() + ", with rating: " + hotel.getRating() + " stars");
            if (hotel.getRating() >= starRating) {
                throw new AssertionError("Hotel rating is below the specified star rating: " + hotel.getRating());
            }
        }
    }

    /**
     * Resets the price filter by clearing the input fields.
     */
    public void resetPriceFilter() {
        logHelper.logStep("Resetting price filter");
        String sliderMinValue = priceFilterSliderMin.getAttribute("aria-valuemin");
        String sliderMaxValue = priceFilterSliderMax.getAttribute("aria-valuemax");

        simulateSliderDrag(priceFilterSliderMin, Integer.parseInt(sliderMinValue), Integer.parseInt(sliderMaxValue));
        // Reset the max price filter to the maximum value since the slider is set to the minimum value
        setMinPriceFilter(0);
        waitForHotelCardsToLoad();
        simulateSliderDrag(priceFilterSliderMax, Integer.parseInt(sliderMaxValue), Integer.parseInt(sliderMaxValue));
        waitForHotelCardsToLoad();
    }

    private String getSliderAttributes(SelenideElement slider, String attribute) {
        return slider.getAttribute(attribute);
    }

    private void simulateSliderDrag(SelenideElement handle, int targetIndex, int maxIndex) {
        handle.scrollIntoView(true);

        double targetPercentage = ((double) targetIndex / maxIndex) * 100;
        double preDragPercentage = targetPercentage > 0 ? targetPercentage - 1 : targetPercentage + 1;

        executeJavaScript("""
        const handle = arguments[0];
        const targetPercentage = arguments[1];
        const preDragPercentage = arguments[2];

        const rect = handle.getBoundingClientRect();
        const startX = rect.left + rect.width / 2;

        const sliderTrack = handle.closest('.rc-slider');
        const sliderRect = sliderTrack.getBoundingClientRect();

        const eventOptions = { bubbles: true, cancelable: true };

        // Simulate a small move before actual target (to force update)
        const preX = sliderRect.left + (preDragPercentage / 100) * sliderRect.width;
        const endX = sliderRect.left + (targetPercentage / 100) * sliderRect.width;

        handle.dispatchEvent(new MouseEvent('mousedown', { clientX: startX, ...eventOptions }));
        document.dispatchEvent(new MouseEvent('mousemove', { clientX: preX, ...eventOptions }));
        document.dispatchEvent(new MouseEvent('mousemove', { clientX: endX, ...eventOptions }));
        document.dispatchEvent(new MouseEvent('mouseup', { clientX: endX, ...eventOptions }));
        sliderTrack.dispatchEvent(new MouseEvent('mouseup', { clientX: endX, ...eventOptions }));
    """, handle, targetPercentage, preDragPercentage);
    }

    private void simulateSliderDrag2(SelenideElement handle, int targetIndex, int maxIndex) {
        handle.scrollIntoView(true);

        int intermediateIndex = targetIndex == 0 ? 1 : targetIndex - 1;
        double intermediatePercent = ((double) intermediateIndex / maxIndex) * 100;
        double targetPercent = ((double) targetIndex / maxIndex) * 100;

        executeJavaScript("""
        const handle = arguments[0];
        const intermediatePercent = arguments[1];
        const targetPercent = arguments[2];

        const rect = handle.getBoundingClientRect();
        const sliderTrack = handle.closest('.rc-slider');
        const sliderRect = sliderTrack.getBoundingClientRect();

        const startX = rect.left + rect.width / 2;
        const intermediateX = sliderRect.left + (intermediatePercent / 100) * sliderRect.width;
        const endX = sliderRect.left + (targetPercent / 100) * sliderRect.width;

        const eventOpts = { bubbles: true, cancelable: true };

        handle.dispatchEvent(new MouseEvent('mousedown', { clientX: startX, ...eventOpts }));
        document.dispatchEvent(new MouseEvent('mousemove', { clientX: intermediateX, ...eventOpts }));
        document.dispatchEvent(new MouseEvent('mousemove', { clientX: endX, ...eventOpts }));
        document.dispatchEvent(new MouseEvent('mouseup', { clientX: endX, ...eventOpts }));
        sliderTrack.dispatchEvent(new MouseEvent('mouseup', { clientX: endX, ...eventOpts }));
    """, handle, intermediatePercent, targetPercent);
    }

    private void simulateSliderDrag3(SelenideElement handle, int targetIndex, int maxIndex) {
        handle.scrollIntoView(true);

        double percentTarget = ((double) targetIndex / maxIndex) * 100;
        double percentNudgeForward = Math.min(percentTarget + 1, 100);
        double percentNudgeBack = Math.max(percentTarget - 1, 0);

        executeJavaScript("""
        const handle = arguments[0];
        const percentTarget = arguments[1];
        const percentNudgeForward = arguments[2];
        const percentNudgeBack = arguments[3];

        const eventOpts = { bubbles: true, cancelable: true };
        const rect = handle.getBoundingClientRect();
        const sliderTrack = handle.closest('.rc-slider');
        const sliderRect = sliderTrack.getBoundingClientRect();

        const startX = rect.left + rect.width / 2;
        const xNudgeForward = sliderRect.left + (percentNudgeForward / 100) * sliderRect.width;
        const xNudgeBack = sliderRect.left + (percentNudgeBack / 100) * sliderRect.width;
        const xTarget = sliderRect.left + (percentTarget / 100) * sliderRect.width;

        handle.dispatchEvent(new MouseEvent('mousedown', { clientX: startX, ...eventOpts }));
        document.dispatchEvent(new MouseEvent('mousemove', { clientX: xNudgeForward, ...eventOpts }));
        document.dispatchEvent(new MouseEvent('mousemove', { clientX: xNudgeBack, ...eventOpts }));
        document.dispatchEvent(new MouseEvent('mousemove', { clientX: xTarget, ...eventOpts }));
        document.dispatchEvent(new MouseEvent('mouseup', { clientX: xTarget, ...eventOpts }));
        sliderTrack.dispatchEvent(new MouseEvent('mouseup', { clientX: xTarget, ...eventOpts }));
    """, handle, percentTarget, percentNudgeForward, percentNudgeBack);
    }

    /**
     * Verifies that the price filter is reset by checking the input fields.
     */
    public void verifyPriceFilterReset() {
        logHelper.logStep("Verifying that the price filter is reset");

        int minValue = Integer.parseInt(getSliderAttributes(priceFilterSliderMin, priceMinValueAttribute));
        int maxValue = Integer.parseInt(getSliderAttributes(priceFilterSliderMax, priceMaxValueAttribute));

        int currentMinValue = Integer.parseInt(getSliderAttributes(priceFilterSliderMin, priceNowValueAttribute));
        int currentMaxValue = Integer.parseInt(getSliderAttributes(priceFilterSliderMax, priceNowValueAttribute));

        if (currentMinValue == minValue && currentMaxValue == maxValue) {
            logHelper.logStep("Price filter is reset to default values");
        } else {
            String currentMinValueText = getSliderAttributes(minPriceFilter, priceDisplayValueAttribute);
            String currentMaxValueText = getSliderAttributes(maxPriceFilter, priceDisplayValueAttribute);
            throw new AssertionError("Price filter is not reset correctly. Current: " + currentMinValueText + " -> " + currentMaxValueText);
        }

    }
}
