package testDataObject.AGTest;


import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AGDataObject {

    private String place;
    private Occupancy occupancy;
    private PriceFilter priceFilter;
    private int rating;
    private int resultCount;
    private Facilities facilities;
    private List<String> reviewCategories;
}
