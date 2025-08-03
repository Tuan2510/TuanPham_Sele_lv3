package testDataObject.AGTest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PriceFilter {
    private int priceMin;
    private int priceMax;
}
