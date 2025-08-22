package testDataObject.AGTest;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
public class AGDataObject {

    private String place;
    private Occupancy occupancy;
    private int resultCount;
    private Facilities facilities;

    private String flightOrigin;
    private String flightDestination;
    private FlightOccupancy flightOccupancy;
    private FlightClass flightClass;
}
