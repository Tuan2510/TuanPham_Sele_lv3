package testDataObject.VJTest;

public class FlightDataObject {

    private String departmentLocation;
    private String destinationLocation;
    private int departAfterDays;
    private int returnAfterDays;
    private FlightPassengerDataObject flightPassengerDataObject;

    public FlightDataObject() {
    }

    public FlightDataObject(FlightPassengerDataObject flightPassengerDataObject, int returnAfterDays, int departAfterDays, String destinationLocation, String departmentLocation) {
        this.flightPassengerDataObject = flightPassengerDataObject;
        this.returnAfterDays = returnAfterDays;
        this.departAfterDays = departAfterDays;
        this.destinationLocation = destinationLocation;
        this.departmentLocation = departmentLocation;
    }

    public String getDepartmentLocation() {
        return departmentLocation;
    }

    public void setDepartmentLocation(String departmentLocation) {
        this.departmentLocation = departmentLocation;
    }

    public String getDestinationLocation() {
        return destinationLocation;
    }

    public void setDestinationLocation(String destinationLocation) {
        this.destinationLocation = destinationLocation;
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
