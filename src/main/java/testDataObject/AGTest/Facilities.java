package testDataObject.AGTest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import utils.LanguageManager;

@Getter
@AllArgsConstructor
public enum Facilities {
    SWIMMING_POOL("swimming_pool"),
    NON_SMOKING("non_smoking");


    private final String key;

    public String getFacility() {
        return LanguageManager.get(key);
    }
}
