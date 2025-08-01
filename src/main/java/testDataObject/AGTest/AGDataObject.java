package testDataObject.AGTest;


import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AGDataObject {

    private String place;
    private Occupancy occupancy;
    private PriceFilter priceFilter;
    private int rating;
    private int resultCount;
}
