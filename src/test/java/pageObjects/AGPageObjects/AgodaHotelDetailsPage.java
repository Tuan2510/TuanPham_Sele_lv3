package pageObjects.AGPageObjects;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testDataObject.AGTest.Facilities;
import testDataObject.AGTest.Hotel;
import utils.LogHelper;
import utils.TestListener;

import java.util.Map;
import testDataObject.AGTest.ReviewCategory;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.$x;
import static com.codeborne.selenide.Selenide.closeWindow;
import static com.codeborne.selenide.Selenide.switchTo;
import static utils.ElementHelper.scrollToElement;

public class AgodaHotelDetailsPage {
    private static final Logger logger = LoggerFactory.getLogger(AgodaHotelDetailsPage.class);
    private final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    // Locators
    private final SelenideElement hotelName = $("[data-selenium='hotel-header-name']");
    private final SelenideElement hotelDestination = $("[data-selenium='hotel-address-map']");
    private final SelenideElement hotelRating = $("[data-testid='review-tooltip-icon']");

    // Dynamic locators
    private final String hotelAvailableFacility = "//div[@id='abouthotel-features']//span[text()='%s']";
    private final String reviewCategoryScore = "//div[span[text()='%s']]//p";

    // Methods
    private void verifyHotelName(String expectedName) {
        hotelName.shouldHave(Condition.text(expectedName));
    }

    private void verifyHotelDestination(String expectedDestination) {
        expectedDestination = expectedDestination.split("-")[0].trim(); // Extract the first part of the destination
        hotelDestination.shouldHave(Condition.text(expectedDestination));
    }

    private void verifyHasFacility(Facilities facility) {
        SelenideElement facilityItem = $x(hotelAvailableFacility.formatted(facility.getFacility()));
        facilityItem.scrollIntoView(true).shouldBe(Condition.visible);
    }

    /**
     * Verifies the review scores for various categories on the hotel details page.
     *
     * @param expectedScores A map of expected review scores where the key is the category name and the value is the score.
     */
    public void verifyReviewScores(Map<ReviewCategory, Float> expectedScores) {
        logHelper.logStep("Verifying review scores for hotel [%s]", hotelName.getText());
        logHelper.logStep("Expected review scores: %s", expectedScores);
        scrollToElement(hotelRating);
        hotelRating.hover();

        for (Map.Entry<ReviewCategory, Float> entry : expectedScores.entrySet()) {
            SelenideElement score = $x(String.format(reviewCategoryScore, entry.getKey().getCategory()));
            try {
                score.shouldBe(Condition.visible).shouldHave(Condition.text(String.valueOf(entry.getValue())));
            } catch (AssertionError e) {
                throw new AssertionError(
                        String.format("Review score for category '%s' does not match. Expected: '%s', Found: '%s'",
                                entry.getKey().getCategory(), entry.getValue(), score.getText()), e);
            }
        }
    }

    /**
     * Verifies the hotel information and facilities on the hotel details page.
     *
     * @param hotel The hotel object containing the hotel name and address.
     * @param facilities The facilities object containing the facility to verify.
     */
    public void verifyHotelInfoAndFacilities(Hotel hotel, Facilities facilities) {
        logHelper.logStep("Verifying hotel information and facilities for hotel name: %s, address: %s, facility: %s",
                hotel.getName(), hotel.getAddress(), facilities.getFacility());
        verifyHotelName(hotel.getName());
        verifyHotelDestination(hotel.getAddress());
        verifyHasFacility(facilities);
    }

    /**
     * Navigates back to the search results page by closing the current window and switching to the previous one.
     */
    public void goBackToSearchResultsPage() {
        closeWindow();
        switchTo().window(1);
    }

}
