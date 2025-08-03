package testDataObject.AGTest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Hotel {
    private String name;
    private String address;
    private float rating;
    private int price;
}
