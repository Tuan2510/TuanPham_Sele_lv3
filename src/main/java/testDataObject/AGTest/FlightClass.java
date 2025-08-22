package testDataObject.AGTest;

public enum FlightClass {
    ECONOMY("economy"),
    PREMIUM_ECONOMY("premium-economy"),
    BUSINESS("business"),
    FIRST_CLASS("first");

    private final String key;

    FlightClass(String key) {
        this.key = key;
    }

    public String getFlightClass() {
        return key;
    }
}
