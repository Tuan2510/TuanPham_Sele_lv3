package testDataObject.VJTest;

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

    public FlightDataObject() {
    }

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

    public FlightType getFlightType() {
        return flightType;
    }

    public void setFlightType(FlightType flightType) {
        this.flightType = flightType;
    }

    public String getFlightTypeCode() {
        return flightTypeCode;
    }

    public void setFlightTypeCode(String flightTypeCode) {
        this.flightTypeCode = flightTypeCode;
    }

    public String getDepartmentLocation() {
        return departmentLocation;
    }

    public void setDepartmentLocation(String departmentLocation) {
        this.departmentLocation = departmentLocation;
    }

    public String getDepartmentLocationCode() {
        return departmentLocationCode;
    }

    public void setDepartmentLocationCode(String departmentLocationCode) {
        this.departmentLocationCode = departmentLocationCode;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
    }

    public String getDestinationLocationCode() {
        return destinationLocationCode;
    }

    public void setDestinationLocationCode(String destinationLocationCode) {
        this.destinationLocationCode = destinationLocationCode;
    }

    public int getDepartAfterDays() {
        return departAfterDays;
    }

    public void setDepartAfterDays(int departAfterDays) {
        this.departAfterDays = departAfterDays;
    }

    public int getReturnAfterDays() {
        return returnAfterDays;
    }

    public void setReturnAfterDays(int returnAfterDays) {
        this.returnAfterDays = returnAfterDays;
    }

    public FlightPassengerDataObject getFlightPassengerDataObject() {
        return flightPassengerDataObject;
    }

    public void setFlightPassengerDataObject(FlightPassengerDataObject flightPassengerDataObject) {
        this.flightPassengerDataObject = flightPassengerDataObject;
    }
}
