package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

public class TestListener implements ITestListener{
    private static final ThreadLocal<ExtentTest> testThread = new ThreadLocal<>();
    private static ExtentReports extent = ExtentManager.getInstance();

    @Override
    public void onTestStart(ITestResult result) {
        ExtentTest test = extent.createTest(result.getMethod().getMethodName());
        testThread.set(test);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        testThread.get().log(Status.PASS, "Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        if (result!=null) {
            testThread.get().log(Status.FAIL, "Test failed: " + result.getThrowable() );
        } else {
            testThread.get().log(Status.FAIL, "Test failed: ");
        }
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (result!=null) {
            testThread.get().log(Status.SKIP, "Test failed: " + result.getThrowable() );
        } else {
            testThread.get().log(Status.SKIP, "Test failed: ");
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

}
