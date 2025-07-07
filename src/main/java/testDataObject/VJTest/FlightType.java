package testDataObject.VJTest;

import lombok.Getter;

@Getter
public enum FlightType {
    ROUND_TRIP("roundTrip"),
    ONE_WAY("oneway");

    private final String value;

    FlightType(String value) {
        this.value = value;
    }
}
