package utils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.testng.annotations.DataProvider;

import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.nio.charset.StandardCharsets;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestDataProvider {

    //get test data from resources [className].json with [methodName]
    @DataProvider(name = "getData")
    public static Object[][] getData(Method method) throws Exception {
        // Load JSON file from resources
        String className = method.getDeclaringClass().getSimpleName();

        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(TestDataProvider.class.getClassLoader()
                        .getResourceAsStream("testdata/" + className + ".json")), StandardCharsets.UTF_8)) {

            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, List<Map<String, String>>>>() {}.getType();
            Map<String, List<Map<String, String>>> jsonData = gson.fromJson(reader, mapType);

            // Load the data with method name
            List<Map<String, String>> dataList = jsonData.get(method.getName());

            //convert to Object
            Object[][] result = new Object[dataList.size()][1];

            for (int i = 0; i < dataList.size(); i++) {
                Hashtable<String, String> table = new Hashtable<>(dataList.get(i));
                result[i][0] = table;
            }

            return result;
        }
    }

}
