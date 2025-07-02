package testDataObject.VJTest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightPassengerDataObject {

    private int adults;
    private int children;
    private int infants;

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
