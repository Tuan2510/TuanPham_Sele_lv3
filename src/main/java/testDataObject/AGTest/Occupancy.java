package testDataObject.AGTest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Occupancy {
    private int roomCount;
    private int adultCount;
    private int childCount;

    public String toString() {
        StringBuilder occupancyString = new StringBuilder();
        occupancyString.append(roomCount).append(" Room");
        if (roomCount > 1) {
            occupancyString.append("s");
        }
        occupancyString.append(", ").append(adultCount).append(" Adult");
        if (adultCount > 1) {
            occupancyString.append("s");
        }
        if (childCount > 0) {
            occupancyString.append(", ").append(childCount).append(" Children");
            if (childCount > 1) {
                occupancyString.append("ren");
            }
        }
        return occupancyString.toString();
    }
}
