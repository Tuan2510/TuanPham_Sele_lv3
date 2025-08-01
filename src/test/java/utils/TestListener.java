package utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.codeborne.selenide.Selenide;
import commons.Constants;
import io.qameta.allure.Description;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IExecutionListener;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;
import org.testng.TestNG;
import reportManager.AllureManager;
import reportManager.ExtentManager;

import java.io.File;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Collections;

public class TestListener implements ITestListener, IExecutionListener {
    public static final TestListener INSTANCE = new TestListener();

    private static final Logger logger = LoggerFactory.getLogger(TestListener.class);
    private static boolean rerunInProgress = false;

    private static final ExtentReports extent = ExtentManager.getInstance();
    private static final ThreadLocal<ExtentTest> currentNode = new ThreadLocal<>();
    private static final java.util.Map<String, ExtentTest> classNodes = new java.util.concurrent.ConcurrentHashMap<>();
    private static final DateTimeFormatter TS_FMT = DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss");

    @Override
    public void onExecutionStart() {
    }

    @Override
    public void onStart(ITestContext context) {
        try {
            AllureManager.copyAllureResult();
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }

        AllureManager.setupAllureReporting();
    }

    @Override
    public void onTestStart(ITestResult result) {
        String className = result.getTestClass().getRealClass().getSimpleName();
        ExtentTest classNode = classNodes.computeIfAbsent(className, extent::createTest);

        //create a node for the test method with getDataNo in the name
        String methodName = result.getMethod().getMethodName();
        Object[] params = result.getParameters();
        if (params != null && params.length > 0) {
            for (Object param : params) {
                try {
                    java.lang.reflect.Method getter = param.getClass().getMethod("getDataNo");
                    Object val = getter.invoke(param);
                    if (val != null) {
                        methodName = methodName + "-" + val.toString();
                        break;
                    }
                } catch (NoSuchMethodException | IllegalAccessException | java.lang.reflect.InvocationTargetException ignored) {
                    // ignore if param does not have getDataNo method
                }
            }
        }

        ExtentTest node = classNode.createNode(methodName);
        currentNode.set(node);
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
                logger.warn("ExtentTest node was null on failure of: {}", result.getName());
            }
        } catch (Exception ignored) {}
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        if (currentNode.get() != null) {
            currentNode.get().skip(result.getThrowable());
        } else {
            logger.warn("ExtentTest node was null on skip of: {}", result.getName());
        }
    }

    @Override
    public void onFinish(ITestContext context) {
        extent.flush();
    }

    @Override
    public void onExecutionFinish() {
        if (rerunInProgress || !"afterDone".equalsIgnoreCase(Constants.RETRY_STRATEGY)) {
            return;
        }

        for (int i = 0; i < Constants.MAX_RETRY; i++) {
            String failedSuite = locateFailedSuite();
            if (failedSuite == null) {
                break;
            }
            TestNG testng = new TestNG();
            testng.setTestSuites(Collections.singletonList(failedSuite));
            testng.addListener(TestListener.INSTANCE);
            testng.addListener(new io.qameta.allure.testng.AllureTestNg());
            rerunInProgress = true;
            try {
                testng.run();
            } finally {
                rerunInProgress = false;
            }
        }
    }

    private String locateFailedSuite() {
        String[] paths = {
                "target/surefire-reports/testng-failed.xml",
                "test-output/testng-failed.xml"
        };
        for (String p : paths) {
            File f = new File(p);
            if (f.exists()) {
                return f.getAbsolutePath();
            }
        }
        return null;
    }


    public void addStep(String stepDesc){
        currentNode.get().info(stepDesc);
    }
}
