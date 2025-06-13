package base;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.SkipException;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import utils.DriverFactory;
import utils.RunConfigReader;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Hashtable;

public class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);
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
        logger.info("Before Method - Start");
        DriverFactory.initDriver();

        runTypeConfig = RunConfigReader.get("runType");
        if ((testArgs.length > 0) && (testArgs[0] instanceof Hashtable)) {
            Hashtable<String, String> data = (Hashtable<String, String>) testArgs[0];
            String runTypeFromData = data.get("RunType");
            if (!"ALL".equalsIgnoreCase(runTypeConfig) && !runTypeFromData.equalsIgnoreCase(runTypeConfig)) {
                logger.info("Skipping test due to mismatched RunType. Expected: {}, Found: {}", runTypeConfig, runTypeFromData);
                throw new SkipException("Skipping test due to mismatched RunType. Expected: " + runTypeConfig + ", Found: " + runTypeFromData);
            }
        }

        logger.info("Before Method - End");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        logger.info("After Method - Start");

        logger.info("--------------------------------------------------");
        DriverFactory.quitDriver();

        logger.info("After Method - End");
    }

    private static void initReportPaths(){
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));

        String baseDir = System.getProperty("user.dir");
        htmlReportPath = baseDir + "/extentV5/" + timestamp + "/" + timestamp + ".html";
        // allureResultsPath = baseDir + "/allure-reports/" + timestamp;

        // Create directories if not exist
        new File(htmlReportPath).getParentFile().mkdirs();
        // new File(allureResultsPath).mkdirs();

        System.setProperty("extent.report.path", htmlReportPath);
        // System.setProperty("allure.results.directory", allureResultsPath);
    }
}
