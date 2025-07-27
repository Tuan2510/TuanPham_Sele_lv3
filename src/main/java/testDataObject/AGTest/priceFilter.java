package testDataObject.AGTest;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class priceFilter {
    private int priceMin;
    private int priceMax;
}
