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
        occupancyString.append(roomCount).append(" Rooms");
        if (roomCount > 1) {
            occupancyString.append("s");
        }
        occupancyString.append(", ").append(adultCount).append(" Adults");
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
