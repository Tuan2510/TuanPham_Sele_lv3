package testDataObject.AGTest;

import lombok.AllArgsConstructor;
import utils.LanguageManager;

@AllArgsConstructor
public enum ReviewCategory {
    CLEANLINESS("cleanliness"),
    FACILITIES("facilities"),
    SERVICE("service"),
    LOCATION("location"),
    VALUE_FOR_MONEY("value_for_money");

    private final String key;

    public String getCategory() {
        return LanguageManager.get(key);
    }
}
