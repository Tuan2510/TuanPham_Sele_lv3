package utils;

import commons.Constants;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer{

    private int attempt = 0;
    private static final int MAX_RETRIES = Constants.MAX_RETRY;
    private static final String RETRY_STRATEGY = Constants.RETRY_STRATEGY;

    @Override
    public boolean retry(ITestResult result) {
        if (attempt < MAX_RETRIES) {
            attempt++;
            return true;
        }
        return false;
    }
}
