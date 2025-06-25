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

    public String getStringAdults(){
        return this.adults + " Adults";
    }

    public String getStringChildren(){
        if (this.children > 0) return this.children + " Childrens";
        else return "";
    }

    public String getStringInfants(){
        if (this.infants > 0) return this.infants + " Infants";
        else return "";
    }

    public String getStringFlightPassenger(){
        String result = this.getStringAdults();

        if(!this.getStringChildren().equalsIgnoreCase("")){
            result = result + ", " + this.getStringChildren();
        }

        if(!this.getStringInfants().equalsIgnoreCase("")){
            result = result + ", " + this.getStringInfants();
        }

        return result;
    }
}
