package utils;

import testDataObject.LeapFrog.ProductInfo;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class ProductReportGenerator {

    public static void logIdentical(List<ProductInfo> identical,
                                    Map<String, Integer> rowMap,
                                    LogHelper logHelper) {
        for (ProductInfo p : identical) {
            int row = rowMap.getOrDefault(p.getName(), -1);
            logHelper.logStep(String.format("row=%d, name=[%s] is identical with website", row, p.getName()));
        }
    }

    public static void logAdded(List<ProductInfo> added, LogHelper logHelper) {
        for (ProductInfo p : added) {
            logHelper.logStep(String.format("name=[%s] only exists on website", p.getName()));
        }
    }

    public static void logUpdated(List<ProductInfo> updated,
                                  List<ProductInfo> expectedList,
                                  LogHelper logHelper) {
        for (ProductInfo p : updated) {
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

    public static void logDeleted(List<ProductInfo> deleted, LogHelper logHelper) {
        for (ProductInfo p : deleted) {
            logHelper.logStep(String.format("row=%d, name=[%s %s] is deleted", p.getRowNo(), p.getName(), p.getAge()));
        }
    }

    public static void logLeapFrogReportSummary(ProductComparator.Report report,
                                                List<ProductInfo> expectedList,
                                                LogHelper logHelper) {
        logHelper.logStep("--------------------------------------------------");
        logHelper.logStep("Total products in Excel: " + report.getTotalExcel());
        logHelper.logStep("Total products on website: " + report.getTotalWeb());

//        //uncomment to log identical products
//        Map<String, Integer> rowMap = expectedList.stream()
//                .collect(Collectors.toMap(ProductInfo::getName, ProductInfo::getRowNo, (a, b) -> a));
//
//        if (!report.getIdentical().isEmpty()) {
//            logHelper.logStep("--------------------------------------------------");
//            logHelper.logStep(String.format("----- Identical products: %s -----", report.getIdentical().size()));
//            logIdentical(report.getIdentical(), rowMap, logHelper);
//        }

        if (!report.getAdded().isEmpty()) {
            logHelper.logStep("--------------------------------------------------");
            logHelper.logStep(String.format("----- Added products: %s -----", report.getAdded().size()));
            logAdded(report.getAdded(), logHelper);
        }

        if (!report.getUpdated().isEmpty()) {
            logHelper.logStep("--------------------------------------------------");
            logHelper.logStep(String.format("----- Updated products: %s -----", report.getUpdated().size()));
            logUpdated(report.getUpdated(), expectedList, logHelper);
        }

        if (!report.getDeleted().isEmpty()) {
            logHelper.logStep("--------------------------------------------------");
            logHelper.logStep(String.format("----- Deleted products: %s -----", report.getDeleted().size()));
            logDeleted(report.getDeleted(), logHelper);
        }
    }
}
