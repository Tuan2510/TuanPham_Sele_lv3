package pageObjects.VJPageObjects;

import com.codeborne.selenide.SelenideElement;

import static com.codeborne.selenide.Selenide.$x;

public class VJSelectTicketPage {
    private final SelenideElement closeAdPanelButton = $x("//button[contains(@class, 'MuiButtonBase-root') and @aria-label='close']");
    private final SelenideElement lowestTicket = $x("");

    private final SelenideElement continueButton = $x("//button[contains(@class, 'MuiButtonBase-root MuiButton-root MuiButton-contained')]");


}
