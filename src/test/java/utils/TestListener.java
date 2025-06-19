package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.codeborne.selenide.Selenide;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import reportManager.ExtentManager;
import reportManager.ReportPathsInitializer;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class TestListener implements ITestListener, IExecutionListener {
    public static final TestListener INSTANCE = new TestListener();

    private static final ExtentReports extent = ExtentManager.getInstance();
    private static final ThreadLocal<ExtentTest> currentNode = new ThreadLocal<>();

    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");
    private final Map<String, ExtentTest> parents = new ConcurrentHashMap<>();

    @Override
    public void onExecutionStart() {
        ReportPathsInitializer.createReportFolders();
        System.setProperty("allure.results.directory", ReportPathsInitializer.ALLURE_RESULTS_DIR);
    }
    @Override public void onExecutionFinish() {}

    @Override
    public void onTestStart(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        String methodName = result.getMethod().getMethodName();

        @SuppressWarnings("unchecked")
        Map<String, String> data = (Map<String, String>) result.getParameters()[0];
        String testName = className + "." + methodName;

        ExtentTest parent = parents.computeIfAbsent(testName, extent::createTest);

        Object retryName = result.getAttribute("retryName");
        String nodeName = retryName != null
                ? retryName.toString()
                : "DataNo=" + data.get("dataNo") + ": " + data.get("TestPurpose");

        ExtentTest node = parent.createNode(nodeName);

        if (retryName != null) {
            String uuid = (String) result.getAttribute("ALLURE_UUID");
            if (uuid != null) {
                io.qameta.allure.Allure.getLifecycle()
                        .updateTestCase(uuid, tr -> tr.setName(nodeName));
            }
        }

        currentNode.set(node);
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        currentNode.get().pass("Test passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        ExtentTest node = currentNode.get();

        // attempt a Selenide screenshot (swallow errors)
        try {
            String ts   = TS_FMT.format(LocalDateTime.now());
            String shot = result.getMethod().getMethodName() + "_" + ts;
            String path = Selenide.screenshot(shot) + ".png";
            node.addScreenCaptureFromPath(path);
        } catch (Exception ignored) {}

        node.fail(result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        currentNode.get().skip(result.getThrowable());
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    public void addStep(String stepDesc){
        currentNode.get().info(stepDesc);
    }
}
