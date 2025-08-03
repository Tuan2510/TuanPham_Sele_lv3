package testDataObject.AGTest;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum Facilities {
    SWIMMING_POOL("Swimming pool"),
    NON_SMOKING("Non-smoking");

    private final String facility;
}
