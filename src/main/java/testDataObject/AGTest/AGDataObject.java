package testDataObject.AGTest;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AGDataObject {

    private String place;
    private String checkInDay;
    private int stayDurationDays;
    private Occupancy occupancy;
    private PriceFilter priceFilter;
    private int rating;
    private int resultCount;
}
