package testcases.LeapFrog;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import testcases.TestBase;
import utils.ProductReportGenerator;
import utils.RetryAnalyzer;
import utils.TestListener;
import utils.ExcelReader;
import utils.WebPageLoader;
import utils.ProductComparator;
import testDataObject.LeapFrog.ProductInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static commons.Constants.LEAP_FROG_EXCEL_PATH;

@Listeners({TestListener.class})
public class LeapFrogContentTest extends TestBase{

    @Test(description = "Verify the product content on LeapFrog website",
    retryAnalyzer = RetryAnalyzer .class, groups = "LF_Regression")
    public void verifyLeapFrogContent() throws Exception {
        logHelper.logStep("Step #1: Load expected data from Excel file");
        List<ProductInfo> expectedList = ExcelReader.readProducts(LEAP_FROG_EXCEL_PATH);

        logHelper.logStep("Step #2: Load product data from website via threads");
        WebPageLoader loader = new WebPageLoader(50);
        int numberOfPages = loader.fetchLeapFrogNumberOfPages();
        List<ProductInfo> webList = loader.loadLeapFrogPages(1, numberOfPages);

        logHelper.logStep("Step #3: Compare expected data with website data");
        ProductComparator.Report report = ProductComparator.compare(expectedList, webList);

        logHelper.logStep("Step #4: Generate report of differences");
        ProductReportGenerator.logLeapFrogReportSummary(report, expectedList, logHelper);

        Assert.assertTrue(report.getUpdated().isEmpty(), "Updated records found: " + report.getUpdated().size());
        Assert.assertTrue(report.getDeleted().isEmpty(), "Deleted records found: " + report.getDeleted().size());
    }
}
