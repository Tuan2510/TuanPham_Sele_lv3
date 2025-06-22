package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.codeborne.selenide.Selenide;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import reportManager.AllureManager;
import reportManager.ExtentManager;
import reportManager.ReportPathsInitializer;
import testDataObject.DataObject;

import java.time.format.DateTimeFormatter;

public class TestListener implements ITestListener, IExecutionListener {
    public static final TestListener INSTANCE = new TestListener();

    private static final ExtentReports extent = ExtentManager.getInstance();
    private static final ThreadLocal<ExtentTest> currentNode = new ThreadLocal<>();

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void onExecutionStart() {
//        System.setProperty("allure.results.directory", ReportPathsInitializer.ALLURE_RESULTS_DIR);
    }

    @Override
    public void onStart(ITestContext context) {
        AllureManager.setupAllureReporting();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String testName = result.getMethod().getMethodName();

        Object[] params = result.getParameters();
        if (params != null && params.length > 0 && params[0] instanceof DataObject data) {
            testName = testName + "---" + data.getDataNo() + ": " + data.getTestPurpose();
        }

        ExtentTest test = extent.createTest(testName);
        currentNode.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        currentNode.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest node = currentNode.get();
        String path = Selenide.screenshot(result.getName()) + ".png";
        try {
            if (node != null) {
                node.addScreenCaptureFromPath(path);
                node.fail(result.getThrowable());
            } else {
                // fallback log if node not initialized
                System.out.println("ExtentTest node was null on failure of: " + result.getName());
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (currentNode.get() != null) {
            currentNode.get().skip(result.getThrowable());
        } else {
            System.out.println("ExtentTest node was null on skip of: " + result.getName());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    @Override public void onExecutionFinish() {}

    public void addStep(String stepDesc){
        currentNode.get().info(stepDesc);
    }
}
