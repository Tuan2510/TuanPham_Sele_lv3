package testDataObject.VJTest;

public class FlightCardDataHolder {
    private FlightCardInfo departFlight;
    private FlightCardInfo returnFlight;

    public FlightCardDataHolder() {
    }

    public FlightCardInfo getDepartFlight() {
        return departFlight;
    }

    public void setDepartFlight(FlightCardInfo departFlight) {
        this.departFlight = departFlight;
    }

    public FlightCardInfo getReturnFlight() {
        return returnFlight;
    }

    public void setReturnFlight(FlightCardInfo returnFlight) {
        this.returnFlight = returnFlight;
    }

}
