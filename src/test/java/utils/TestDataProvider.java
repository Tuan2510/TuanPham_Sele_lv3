package utils;

import com.google.gson.*;
import org.testng.annotations.DataProvider;

import java.io.*;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class TestDataProvider {

    @DataProvider(name = "getData")
    public static Object[][] getData(Method method) throws IOException {
        Class<?> testClass = method.getDeclaringClass();
        String className = testClass.getSimpleName();

        // Convert package name to path
        String packagePath = testClass.getPackage().getName().replace(".", File.separator);

        // Assume data.json is placed in the same directory as the test class source file
        String path = "src/test/java/" + packagePath + "/data.json";
        File jsonFile = new File(path);
        if (!jsonFile.exists()) {
            throw new IOException("data.json not found at path: " + jsonFile.getAbsolutePath());
        }

        // Parse JSON using Gson
        Gson gson = new Gson();
        JsonObject root = gson.fromJson(new FileReader(jsonFile), JsonObject.class);

        JsonArray testCases = root.getAsJsonArray(className);
        List<Object[]> data = new ArrayList<>();

        if (testCases != null) {
            for (JsonElement element : testCases) {
                JsonObject obj = element.getAsJsonObject();
                Hashtable<String, String> map = new Hashtable<>();
                for (String key : obj.keySet()) {
                    map.put(key, obj.get(key).getAsString());
                }
                data.add(new Object[]{map});
            }
        }

        return data.toArray(new Object[0][]);
    }

    @DataProvider(name = "getDataFromResources")
    public static Object[][] getDataFromResources(Method method) throws Exception {
        Class<?> testClass = method.getDeclaringClass();
        String className = testClass.getSimpleName();

        // Construct path: TestData/[package path]/[ClassName].json
        String packagePath = testClass.getPackage().getName().replace('.', '/');
        String resourcePath = "TestData/" + packagePath + "/" + className + ".json";

        InputStream inputStream = testClass.getClassLoader().getResourceAsStream(resourcePath);
        if (inputStream == null) {
            throw new IllegalArgumentException("Cannot find test data file: " + resourcePath);
        }

        // Parse JSON using Gson
        Gson gson = new Gson();
        JsonElement rootElement = JsonParser.parseReader(new InputStreamReader(inputStream));

        JsonArray testCases;
        if (rootElement.isJsonArray()) {
            // If the file contains a plain array (most common)
            testCases = rootElement.getAsJsonArray();
        } else if (rootElement.isJsonObject() && rootElement.getAsJsonObject().has(className)) {
            // Optional support if file has { "YoutubeTest": [ ... ] }
            testCases = rootElement.getAsJsonObject().getAsJsonArray(className);
        } else {
            throw new IllegalArgumentException("Invalid JSON format in: " + resourcePath);
        }

        List<Object[]> data = new ArrayList<>();
        for (JsonElement element : testCases) {
            JsonObject obj = element.getAsJsonObject();
            Hashtable<String, String> map = new Hashtable<>();
            for (String key : obj.keySet()) {
                map.put(key, obj.get(key).getAsString());
            }
            data.add(new Object[]{map});
        }

        return data.toArray(new Object[0][]);
    }

}
