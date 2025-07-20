package testcases;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.DataProvider;
import driver.DriverFactory;
import reportManager.ReportPathsInitializer;
import utils.LogHelper;
import utils.RunConfigReader;
import utils.TestListener;

import java.io.IOException;
import java.lang.reflect.Method;

import static utils.JsonToObjectHelper.getDataByMethodName;

public class TestBase {
    private static final Logger logger = LoggerFactory.getLogger(TestBase.class);
    protected final LogHelper logHelper = new LogHelper(logger, TestListener.INSTANCE);

    static {
        RunConfigReader.loadConfiguration();
    }

    @DataProvider (name = "getData")
    public Object[][] getData(Method method) throws Exception {
        return getDataByMethodName(method);
    }

    @BeforeSuite
    public void beforeSuite() {
        ReportPathsInitializer.createReportFolders();
    }

    @BeforeClass
    public void beforeClass() {

    }

    @BeforeMethod()
    public void beforeMethod(Object[] testArgs) {
    }

    @AfterMethod(alwaysRun = true)
    public void afterMethod() {
        DriverFactory.quitDriver();
    }

    @AfterClass
    public void afterClass() {

    }

    @AfterSuite
    public void afterSuite() throws IOException, InterruptedException{
//        AllureManager.generateAllureReport();
    }
}
