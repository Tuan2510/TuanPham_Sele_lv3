package utils;

import org.testng.IExecutionListener;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AllureDirectorySetter implements IExecutionListener {

    @Override
    public void onExecutionStart() {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String path = System.getProperty("user.dir") + "/allure-reports/" + timestamp;
        System.setProperty("allure.results.directory", path);
    }
}
