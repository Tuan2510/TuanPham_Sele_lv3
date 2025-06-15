package base;

import com.codeborne.selenide.logevents.SelenideLogger;
import io.qameta.allure.selenide.AllureSelenide;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import reportManager.ReportPathsInitializer;
import utils.DriverFactory;
import utils.RunConfigReader;

public class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);
    protected String runTypeConfig;

    static {
        RunConfigReader.loadConfiguration();
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        System.setProperty("allure.results.directory", ReportPathsInitializer.ALLURE_RESULTS_DIR);

        SelenideLogger.addListener("Allure",
                new AllureSelenide()
                        .screenshots(true)      // capture on failure
                        .savePageSource(false)
        );
    }

    @BeforeClass
    public void beforeClass() {

    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Object[] testArgs) {
        logger.info("Before Method - Start");
        DriverFactory.initDriver();

//        runTypeConfig = RunConfigReader.get("runType");
//        if ((testArgs.length > 0) && (testArgs[0] instanceof Hashtable)) {
//            Hashtable<String, String> data = (Hashtable<String, String>) testArgs[0];
//            String runTypeFromData = data.get("RunType");
//            if (!"ALL".equalsIgnoreCase(runTypeConfig) && !runTypeFromData.equalsIgnoreCase(runTypeConfig)) {
//                logger.info("Skipping test due to mismatched RunType. Expected: {}, Found: {}", runTypeConfig, runTypeFromData);
//                throw new SkipException("Skipping test due to mismatched RunType. Expected: " + runTypeConfig + ", Found: " + runTypeFromData);
//            }
//        }

        logger.info("Before Method - End");
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        logger.info("After Method - Start");

        logger.info("--------------------------------------------------");
        DriverFactory.quitDriver();

        logger.info("After Method - End");
    }

    @AfterClass
    public void afterClass() {

    }

    @AfterSuite
    public void afterSuite() {

    }
}
