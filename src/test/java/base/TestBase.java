package base;

import com.codeborne.selenide.Configuration;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import utils.RunConfigReader;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;

import static com.codeborne.selenide.Selenide.closeWebDriver;

public class TestBase {

    protected String runTypeConfig;
    public static String timestamp;
    public static String htmlReportPath;
    public static String allureResultsPath;

    static {
        RunConfigReader.loadConfiguration();
        initReportPaths();
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {

    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Object[] testArgs) {
        System.out.println("Before Method - Start");
        Configuration.browser = "chrome";

        runTypeConfig = RunConfigReader.get("runType");
        if ((testArgs.length > 0) && (testArgs[0] instanceof Hashtable)) {
            Hashtable<String, String> data = (Hashtable<String, String>) testArgs[0];
            String runTypeFromData = data.get("RunType");
            if (!"ALL".equalsIgnoreCase(runTypeConfig) && !runTypeFromData.equalsIgnoreCase(runTypeConfig)) {
                System.out.println("Skipping test due to mismatched RunType. Expected: " + runTypeConfig + ", Found: " + runTypeFromData);
                throw new SkipException("Skipping test due to mismatched RunType. Expected: " + runTypeConfig + ", Found: " + runTypeFromData);
            }
        }

        System.out.println("Before Method - End");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        System.out.println("After Method - Start");

        System.out.println("--------------------------------------------------");
        closeWebDriver();

        System.out.println("After Method - End");
    }

    private static void initReportPaths(){
        timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String baseDir = System.getProperty("user.dir");
        htmlReportPath = baseDir + "/extentV5/" + timestamp + "/"+timestamp+".html";
        allureResultsPath = baseDir + "/allure-reports/" + timestamp;

        // Create directories if not exist
        new File(htmlReportPath).getParentFile().mkdirs();
        new File(allureResultsPath).mkdirs();

        System.setProperty("extent.report.path", htmlReportPath);
        System.setProperty("allure.results.directory", allureResultsPath);

        Configuration.browser = RunConfigReader.get("browserType");
        Configuration.timeout = Long.parseLong(RunConfigReader.get("timeout"));
    }
}
