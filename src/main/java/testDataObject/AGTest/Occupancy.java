package testDataObject.AGTest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class Occupancy {
    private int roomCount;
    private int adultCount;
    private int childCount;
}
