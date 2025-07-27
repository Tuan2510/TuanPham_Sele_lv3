package testDataObject.AGTest;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.DayOfWeek;

@Data
@NoArgsConstructor
public class AGDataObject {

    private String place;
    private String checkInDay;
    private int stayDurationDays;
    private Occupancy occupancy;
    private priceFilter priceFilter;
    private int rating;

}
