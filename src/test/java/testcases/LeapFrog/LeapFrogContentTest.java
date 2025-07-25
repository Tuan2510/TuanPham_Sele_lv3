package testcases.LeapFrog;

import org.testng.Assert;
import org.testng.annotations.Listeners;
import org.testng.annotations.Test;
import testcases.TestBase;
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

        Map<String, Integer> rowMap = expectedList.stream()
                .collect(Collectors.toMap(ProductInfo::getName, ProductInfo::getRowNo, (a, b) -> a));

        logHelper.logStep("Step #4: Generate report of differences");
        if(!report.identical.isEmpty()) {
            logHelper.logStep("--------------------------------------------------");
            logHelper.logStep("Step #4.1: Identical products: " + report.identical.size());
            for (ProductInfo p : report.identical) {
                int row = rowMap.getOrDefault(p.getName(), -1);
                logHelper.logStep(String.format("row=%d, name=[%s] are identical with web", row, p.getName()));
            }
        }

        if(!report.added.isEmpty()) {
            logHelper.logStep("--------------------------------------------------");
            logHelper.logStep("Step #4.2: Added products: " + report.added.size());
            for (ProductInfo p : report.added) {
                logHelper.logStep(String.format("name=[%s] only exists on website", p.getName()));
            }
        }

        if(!report.updated.isEmpty()) {
            logHelper.logStep("--------------------------------------------------");
            logHelper.logStep("Step #4.3: Updated products: " + report.updated.size());
            for (ProductInfo p : report.updated) {
                ProductInfo origin = expectedList.stream()
                        .filter(e -> e.getName().equals(p.getName()))
                        .findFirst().orElse(null);
                int row = origin != null ? origin.getRowNo() : -1;
                String oriAge = origin != null ? origin.getAge() : "";
                String oriPrice = origin != null ? origin.getPrice() : "";
                logHelper.logStep(String.format("row=%d, name=[%s] is changed, original=[%s %s] actual=[%s %s]",
                        row, p.getName(), oriAge, oriPrice, p.getAge(), p.getPrice()));
            }
        }

        if(!report.deleted.isEmpty()) {
            logHelper.logStep("--------------------------------------------------");
            logHelper.logStep("Step #4.4: Deleted products: " + report.deleted.size());
            for (ProductInfo p : report.deleted) {
                logHelper.logStep(String.format("row=%d, name=[%s %s] is deleted", p.getRowNo(), p.getName(), p.getAge()));
            }
        }

        Assert.assertTrue(report.updated.isEmpty(), "Updated records found: " + report.updated.size());
        Assert.assertTrue(report.deleted.isEmpty(), "Deleted records found: " + report.deleted.size());
    }
}
