package testDataObject.VJTest;

import java.time.LocalDate;

public class FlightData {
    private String departmentLocation;
    private String destinationLocation;
    private LocalDate departDate;
    private LocalDate returnDate;
    private PassengerData passengerData;

    public FlightData() {
    }

    public FlightData(String departmentLocation, String destinationLocation, LocalDate departDate, LocalDate returnDate, PassengerData passengerData) {
        this.departmentLocation = departmentLocation;
        this.destinationLocation = destinationLocation;
        this.departDate = departDate;
        this.returnDate = returnDate;
        this.passengerData = passengerData;
    }

    //this constructor is using for oneway flight
    public FlightData(String departmentLocation, String destinationLocation, LocalDate departDate, PassengerData passengerData) {
        this.departmentLocation = departmentLocation;
        this.destinationLocation = destinationLocation;
        this.departDate = departDate;
        this.returnDate = null;
        this.passengerData = passengerData;
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

    public LocalDate getDepartDate() {
        return departDate;
    }

    public void setDepartDate(LocalDate departDate) {
        this.departDate = departDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public PassengerData getPassengerData() {
        return passengerData;
    }

    public void setPassengerData(PassengerData passengerData) {
        this.passengerData = passengerData;
    }
}
