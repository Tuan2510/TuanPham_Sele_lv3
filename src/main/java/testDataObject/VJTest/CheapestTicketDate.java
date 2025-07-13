package testDataObject.VJTest;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
public class CheapestTicketDate {
    private LocalDate departDate;
    private LocalDate returnDate;
}
