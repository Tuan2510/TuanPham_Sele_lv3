package testDataObject.AGTest;

import lombok.Data;

@Data
public class FlightOccupancy {
    private int adultCount;
    private int childCount;
    private int infantCount;

    public FlightOccupancy() {
        this.adultCount = 1; // Default to 1 adult
        this.childCount = 0; // Default to 0 children
        this.infantCount = 0; // Default to 0 infants
    }

    @Override
    public String toString() {
        StringBuilder occupancyString = new StringBuilder();
        occupancyString.append(adultCount).append(" Adult");
        if (adultCount > 1) {
            occupancyString.append("s");
        }
        if (childCount > 0) {
            occupancyString.append(", ").append(childCount).append(" Child");
            if (childCount > 1) {
                occupancyString.append("ren");
            }
        }
        if (infantCount > 0) {
            occupancyString.append(", ").append(infantCount).append(" Infant");
            if (infantCount > 1) {
                occupancyString.append("s");
            }
        }
        return occupancyString.toString();
    }
}
