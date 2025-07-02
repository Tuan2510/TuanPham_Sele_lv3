package testDataObject.VJTest;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlightCardInfo {

    private String flightId;
    private String time;
    private String price;
}
