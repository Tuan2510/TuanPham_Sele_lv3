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
        // Prepare the data path
        Class<?> testClass = method.getDeclaringClass();
        String packagePath = testClass.getPackage().getName()
                .replace('.', '/')
                .replaceFirst("^testcases/", ""); // remove base package prefix if needed
        String className = testClass.getSimpleName();
        String resourcePath = "testdata/" + packagePath + "/" + className + ".json";

        // Load JSON file from resources
        try (InputStreamReader reader = new InputStreamReader(
                Objects.requireNonNull(
                        TestDataProvider.class.getClassLoader().getResourceAsStream(resourcePath),
                        "Test data file not found: " + resourcePath), StandardCharsets.UTF_8))
        {
            Gson gson = new Gson();
            Type mapType = new TypeToken<Map<String, List<Map<String, String>>>>() {}.getType();
            Map<String, List<Map<String, String>>> jsonData = gson.fromJson(reader, mapType);

            // Load the data with method name
            List<Map<String, String>> dataList = jsonData.get(method.getName());

            if (dataList == null || dataList.isEmpty()) {
                throw new RuntimeException("No data found for test method: " + method.getName());
            }

            //use stream to create object
            return dataList.stream()
                    .map(data -> new Object[]{ new Hashtable<>(data) })
                    .toArray(Object[][]::new);
        }
    }

}
