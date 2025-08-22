package testDataObject.AGTest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class FlightDisplayInfo {
    private LocalDate date;
    private LocalTime departureTime;
    private LocalTime arrivalTime;
    private String airline;
    private String flightClass;
}