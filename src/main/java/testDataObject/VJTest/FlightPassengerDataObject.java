package testDataObject.VJTest;

public class FlightPassengerDataObject {

    private int adults;
    private int children;
    private int infants;

    public FlightPassengerDataObject() {
    }

    public FlightPassengerDataObject(int adults, int children, int infants) {
        this.adults = adults;
        this.children = children;
        this.infants = infants;
    }

    public int getAdults() {
        return adults;
    }

    public void setAdults(int adults) {
        this.adults = adults;
    }

    public int getChildren() {
        return children;
    }

    public void setChildren(int children) {
        this.children = children;
    }

    public int getInfants() {
        return infants;
    }

    public void setInfants(int infants) {
        this.infants = infants;
    }
}
