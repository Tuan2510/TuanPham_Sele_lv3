package testDataObject.VJTest;

public class FlightInfo {
    private String flightId;
    private String time;
    private String plane;

    public FlightInfo() {
    }

    public FlightInfo(String flightId, String time, String plane) {
        this.flightId = flightId;
        this.time = time;
        this.plane = plane;
    }

    public String getFlightId() {
        return flightId;
    }

    public void setFlightId(String flightId) {
        this.flightId = flightId;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getPlane() {
        return plane;
    }

    public void setPlane(String plane) {
        this.plane = plane;
    }
}
