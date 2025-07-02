package testDataObject.VJTest;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import utils.LanguageManager;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightPassengerDataObject {

    private int adults;
    private int children;
    private int infants;

    public String getStringAdults(){
        return "%s %s".formatted(this.adults, LanguageManager.get("adults"));
    }

    public String getStringChildren(){
        if (this.children > 0) return "%s %s".formatted(this.children, LanguageManager.get("children"));
        else return "";
    }

    public String getStringInfants(){
        if (this.infants > 0) return "%s %s".formatted(this.infants, LanguageManager.get("infants"));
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
