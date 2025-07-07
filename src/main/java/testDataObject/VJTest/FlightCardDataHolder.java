package testDataObject.VJTest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FlightCardDataHolder {

    private FlightCardInfo departFlight;
    private FlightCardInfo returnFlight;
}
