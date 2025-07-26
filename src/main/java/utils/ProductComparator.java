package utils;

import testDataObject.LeapFrog.ProductInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProductComparator {
    public static class Report {
        public int totalExcel;
        public int totalWeb;
        public final List<ProductInfo> identical = new ArrayList<>();
        public final List<ProductInfo> updated = new ArrayList<>();
        public final List<ProductInfo> deleted = new ArrayList<>();
        public final List<ProductInfo> added = new ArrayList<>();
    }

    public static Report compare(List<ProductInfo> excel, List<ProductInfo> web) {
        Report r = new Report();
        r.totalExcel = excel.size();
        r.totalWeb = web.size();
        // Map website products by unique name for quick lookup
        Map<String, ProductInfo> mapWeb = web.stream()
                .collect(Collectors.toMap(ProductInfo::getName, Function.identity()));

        for (ProductInfo e : excel) {
            ProductInfo w = mapWeb.remove(e.getName());
            if (w == null) {
                r.deleted.add(e);
            } else {
                if (normalizeAge(e.getAge()).equals(normalizeAge(w.getAge())) &&
                        normalizePrice(e.getPrice()).equals(normalizePrice(w.getPrice()))) {
                    r.identical.add(w);
                } else {
                    r.updated.add(w);
                }
            }
        }
        // Anything left in the map was not in Excel => added
        for (ProductInfo remaining : mapWeb.values()) {
            r.added.add(remaining);
        }
        return r;
    }

    private static final Pattern AGE_SPACE = Pattern.compile("\\s*-\\s*");

    private static String normalizeAge(String age) {
        if (age == null) return "";
        return AGE_SPACE.matcher(age).replaceAll("-").trim();
    }

    private static String normalizePrice(String price) {
        if (price == null) return "";
        return price.replace("Price:", "").trim();
    }

}
