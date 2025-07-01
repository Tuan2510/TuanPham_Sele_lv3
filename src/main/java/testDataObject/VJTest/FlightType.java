package testDataObject.VJTest;

public enum FlightType {
    ROUND_TRIP("roundTrip"),
    ONE_WAY("oneway");

    private final String value;

    FlightType(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}
