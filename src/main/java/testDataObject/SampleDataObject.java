package testDataObject;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class SampleDataObject {

    private String dataNo;
    private String TestPurpose;
    private String RunType;
    private String query;
}
