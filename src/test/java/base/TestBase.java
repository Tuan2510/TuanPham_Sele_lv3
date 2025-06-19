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
import reportManager.AllureReportHelper;
import utils.DriverFactory;
import utils.LogHelper;
import utils.RunConfigReader;
import utils.TestListener;

import java.io.IOException;

public class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);
    protected final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    static {
        RunConfigReader.loadConfiguration();
//        ReportPathsInitializer.createReportFolders();
//        System.setProperty("allure.results.directory", ReportPathsInitializer.ALLURE_RESULTS_DIR);
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {

    }

    @BeforeClass
    public void beforeClass() {

    }

    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Object[] testArgs) {
        logger.info("Before Method - Start");
        DriverFactory.initDriver();

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
    public void afterSuite() throws IOException, InterruptedException{
//        AllureReportHelper.moveAllureResultsAndGenerateReport();
    }
}
