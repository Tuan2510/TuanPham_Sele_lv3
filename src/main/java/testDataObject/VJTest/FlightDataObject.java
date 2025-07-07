package testDataObject.VJTest;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class FlightDataObject {

    private FlightType flightType;
    private String flightTypeCode;
    private String departmentLocation;
    private String departmentLocationCode;
    private String destinationLocation;
    private String destinationLocationCode;
    private int departAfterDays;
    private int returnAfterDays;
    private FlightPassengerDataObject flightPassengerDataObject;

    public FlightDataObject(FlightType flightType, String flightTypeCode,
                            FlightPassengerDataObject flightPassengerDataObject,
                            int returnAfterDays, int departAfterDays,
                            String destinationLocation, String departmentLocation) {
        this.flightType = flightType;
        this.flightTypeCode = flightTypeCode;
        this.flightPassengerDataObject = flightPassengerDataObject;
        this.returnAfterDays = returnAfterDays;
        this.departAfterDays = departAfterDays;
        this.destinationLocation = destinationLocation;
        this.departmentLocation = departmentLocation;
    }
}
