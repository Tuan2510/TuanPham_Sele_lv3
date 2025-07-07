package utils;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class JsonToObjectHelper {

    /**
     * Load data from json file, filter for current method
     */
    public static Object[][] getDataByMethodName(Method method) throws Exception {
        String className = method.getDeclaringClass().getSimpleName();
        String packagePath = method.getDeclaringClass().getPackage().getName()
                .replace('.', '/')
                .replaceFirst("^testcases/", "");
        String resourcePath = "testdata/" + packagePath + "/" + className + ".json";

        Gson gson = new Gson();
        JsonObject jsonObject;
        try (InputStream inputStream = method.getDeclaringClass().getClassLoader().getResourceAsStream(resourcePath);
             InputStreamReader reader = new InputStreamReader(Objects.requireNonNull(inputStream), StandardCharsets.UTF_8)) {
            jsonObject = gson.fromJson(reader, JsonObject.class);

            JsonArray jsonArray = jsonObject.getAsJsonArray(method.getName());

            Class<?> paramType = method.getParameterTypes().length > 0 ? method.getParameterTypes()[0] : Object.class;
            Object[] dataArray = (Object[]) gson.fromJson(jsonArray, Array.newInstance(paramType, 0).getClass());

            Object[][] result = new Object[dataArray.length][1];
            for (int i = 0; i < dataArray.length; i++) {
                result[i][0] = dataArray[i];
            }
            return result;

        } catch (Exception e) {
            throw new RuntimeException("Failed to load test data from: " + resourcePath, e);
        }
    }
}
