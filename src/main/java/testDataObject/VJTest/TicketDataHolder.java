package testDataObject.VJTest;

public class TicketDataHolder {
    private static final ThreadLocal<FlightInfo> departFlight = new ThreadLocal<>();
    private static final ThreadLocal<FlightInfo> returnFlight = new ThreadLocal<>();

    public TicketDataHolder() {
    }

    public FlightInfo getDepartFlight() {
        return departFlight.get();
    }

    public void setDepartFlight(FlightInfo departFlight) {
        TicketDataHolder.departFlight.set(departFlight);
    }

    public FlightInfo getReturnFlight() {
        return returnFlight.get();
    }

    public void setReturnFlight(FlightInfo returnFlight) {
        TicketDataHolder.returnFlight.set(returnFlight);
    }

    public void clear() {
        departFlight.remove();
        returnFlight.remove();
    }

}
