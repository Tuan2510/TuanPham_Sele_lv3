package utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

import static com.codeborne.selenide.Selenide.*;

public class ElementHelper {
    private static final Logger logger = LoggerFactory.getLogger(ElementHelper.class);
    /**
     * Switch to an iframe contents
     */
    public static void switchToIframe(Object frameTarget) {
        if (frameTarget instanceof String) {
            switchTo().frame((String) frameTarget);
        } else if (frameTarget instanceof SelenideElement) {
            switchTo().frame((SelenideElement) frameTarget);
        } else {
            throw new IllegalArgumentException("Unsupported iframe identifier: " + frameTarget);
        }
    }

    /**
     * Switch back to main contents
     */
    public static void switchToDefault() {
        switchTo().defaultContent();
    }

    /**
     * Click on an element after wait for it visible and enable to click
     */
    public static void clickWhenReady(SelenideElement selector, int timeoutSeconds) {
        try {
//            sleep(500);
            selector.scrollIntoView(true);
            executeJavaScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", selector);
            selector.shouldBe(Condition.visible, Duration.ofSeconds(timeoutSeconds))
                    .shouldBe(Condition.enabled, Duration.ofSeconds(timeoutSeconds));
            selector.click();
        } catch (Exception e) {
            // Fallback to JS click if standard click fails
            logger.warn("Standard click failed, trying JavaScript click. Reason: {}", e.getMessage());
            executeJavaScript("arguments[0].click();", selector);
        }
    }

    /**
     * Click when ready with default wait time 10s
     */
    public static void clickWhenReady(SelenideElement selector) {
        clickWhenReady(selector, 10);
    }

    /**
     * Scroll the page to the bottom using JavaScript.
     */
    public static void scrollToPageBottom() {
        executeJavaScript("window.scrollTo(0, document.body.scrollHeight);");
    }

    /**
     * Scroll the page to the top using JavaScript.
     */
    public static void scrollToPageTop() {
        executeJavaScript("window.scrollTo(0, 0);");
    }

    /**
     * Scroll the page by the specified pixel amount (positive = down, negative = up).
     * @param x Horizontal scroll
     * @param y Vertical scroll
     */
    public static void scrollBy(int x, int y) {
        executeJavaScript("window.scrollBy(arguments[0], arguments[1]);", x, y);
    }

    /**
     * Scroll to a specific element using scrollIntoView.
     * @param element The SelenideElement to scroll to
     */
    public static void scrollToElement(SelenideElement element) {
        element.scrollIntoView(true);
        element.shouldBe(Condition.visible, Duration.ofSeconds(5));
    }

    /**
     * Scroll to bottom gradually (useful for infinite scroll pages).
     * @param steps Number of scroll steps
     * @param pixelsPerStep Number of pixels to scroll per step
     * @param delayMillis Delay between scroll steps
     */
    public static void scrollToBottomWithSteps(int steps, int pixelsPerStep, int delayMillis) {
        for (int i = 0; i < steps; i++) {
            scrollBy(0, pixelsPerStep);
            sleep(delayMillis);
        }
    }

    public static boolean isElementDisplayed(SelenideElement element, int timeoutSeconds) {
        try {
            element.shouldBe(Condition.visible, Duration.ofSeconds(timeoutSeconds));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public static boolean isElementDisplayed(SelenideElement element){
        return isElementDisplayed(element, 10);
    }
}
