package utils;

//import com.fasterxml.jackson.databind.JsonNode;
//import com.fasterxml.jackson.databind.ObjectMapper;
import io.qameta.allure.internal.shadowed.jackson.databind.JsonNode;
import io.qameta.allure.internal.shadowed.jackson.databind.ObjectMapper;
import org.testng.annotations.DataProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TestDataProvider {

    @DataProvider(name = "getData")
    public static Object[][] getData(Method method) throws IOException {
        String className = method.getDeclaringClass().getSimpleName();
        String path = "src/test/java/testcases/" + className.toLowerCase() + "/data.json";

        ObjectMapper mapper = new ObjectMapper();
        JsonNode root = mapper.readTree(new File(path));
        JsonNode testCases = root.get(className);

        List<Object[]> data = new ArrayList<>();
        if (testCases != null && testCases.isArray()) {
            for (JsonNode node : testCases) {
                Hashtable<String, String> map = new Hashtable<>();
                node.fieldNames().forEachRemaining(key -> map.put(key, node.get(key).asText()));
                data.add(new Object[]{map});
            }
        }
        return data.toArray(new Object[0][]);
    }
}
