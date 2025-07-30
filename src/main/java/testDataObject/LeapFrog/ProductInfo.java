package testDataObject.LeapFrog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductInfo {
    private int rowNo;
    private String name;
    private String age;
    private String price;

    public ProductInfo(String name, String age, String price) {
        this(-1, name, age, price);
    }

}
