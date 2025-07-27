package utils;

import com.codeborne.selenide.Condition;
import com.codeborne.selenide.SelenideElement;

import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;

public class DatePickerHelper {

    private final SelenideElement calendar;
    private final SelenideElement nextMonthButton;
    private final SelenideElement prevMonthButton;
    private final SelenideElement targetDate;

    public DatePickerHelper(SelenideElement calendar, SelenideElement nextMonthButton, SelenideElement prevMonthButton, SelenideElement targetDate) {
        this.calendar = calendar;
        this.nextMonthButton = nextMonthButton;
        this.prevMonthButton = prevMonthButton;
        this.targetDate = targetDate;
    }

    public void selectDate(LocalDate date) {
        alignDatePickerToMonth(date);
        targetDate.click();
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
        String text = calendar.shouldBe(Condition.visible).shouldNotHave(Condition.exactText("")).getText().trim();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMMM yyyy");
        return YearMonth.parse(text, formatter);
    }
}
