package utils;

import testDataObject.LeapFrog.ProductInfo;

import java.util.List;
import java.util.Map;

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
}
