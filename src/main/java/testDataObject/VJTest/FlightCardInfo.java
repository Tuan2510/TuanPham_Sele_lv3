package testDataObject.VJTest;

public class FlightCardInfo {
    private String flightId;
    private String time;
    private String price;

    public FlightCardInfo() {
    }

    public FlightCardInfo(String flightId, String time, String price) {
        this.flightId = flightId;
        this.time = time;
        this.price = price;
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

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }
}
