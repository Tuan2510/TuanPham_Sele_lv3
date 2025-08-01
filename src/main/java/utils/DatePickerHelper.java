package utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.Duration;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

import static com.codeborne.selenide.Selenide.$;

public class DatePickerHelper {

    private final SelenideElement calendar;
    private final SelenideElement nextMonthButton;
    private final SelenideElement prevMonthButton;
    private final String targetDateCssSelector;

    public DatePickerHelper(SelenideElement calendar, SelenideElement nextMonthButton, SelenideElement prevMonthButton, String targetDate) {
        this.calendar = calendar;
        this.nextMonthButton = nextMonthButton;
        this.prevMonthButton = prevMonthButton;
        this.targetDateCssSelector = targetDate;
    }

    public void selectDateCss(LocalDate date) {
        alignDatePickerToMonth(date);
        $(targetDateCssSelector.formatted(date)).click();
    }

    private void alignDatePickerToMonth(LocalDate targetDate) {
        YearMonth current = getCurrentMonth();
        YearMonth target = YearMonth.from(targetDate);

        while (!current.equals(target)) {
            if (current.isBefore(target)) {
                nextMonthButton.click();
            } else {
                prevMonthButton.click();
            }
            current = getCurrentMonth();
        }
    }

    private YearMonth getCurrentMonth() {
        String text = calendar.shouldBe(Condition.visible, Duration.ofSeconds(10)).shouldNotHave(Condition.exactText("")).getText().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return YearMonth.parse(text, formatter);
    }
}
