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
import testDataObject.AGTest.ReviewCategory;
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
    private final String filterOption = "//div[@id='SideBarLocationFilters']//span[contains(text(),'%s')]";

    private final String hotelCategoryRating = ".//div[@data-element-name='property-card-review']/div/div";
    private final String hotelCategoryScore = "//li[span[text()='%s']]/strong";

    //Methods
    /**
     * Verifies that the search results page is displayed by checking the URL.
     */
    public void verifyPageIsDisplayed() {
        switchTo().window(1);
        webdriver().shouldHave(urlContaining("/search?"));
        logHelper.logStep("Search results page is displayed with URL: %s", webdriver().driver().getCurrentFrameUrl());
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
     * Filters the hotel results by the facility.
     */
    public void filterByFacilities(Facilities facility) {
        logHelper.logStep("Filtering results by %s facility", facility.getFacility());
        SelenideElement targetFilter = $x(String.format(filterOption, facility.getFacility()));
        scrollToElement(targetFilter);
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
        waitForHotelCardToLoad(card);
        scrollToPageTop();
        Hotel hotel = new Hotel();
        hotel.setName(getSafeText(card, hotelNameCss));
        hotel.setAddress(getSafeText(card, hotelAddressCss));
        card.click();
        switchTo().window(2);
        return hotel;
    }

}
