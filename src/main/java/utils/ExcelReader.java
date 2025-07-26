package utils;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import testDataObject.LeapFrog.ProductInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    private static final String TITLE_COL = "title";
    private static final String AGE_COL = "age";
    private static final String PRICE_COL = "price";

    public static List<ProductInfo> readProducts(String path) throws IOException {

        try (FileInputStream fis = new FileInputStream(path);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);

            // Determine column indexes from header row
            Row header = sheet.getRow(0);
            int nameCol = -1;
            int ageCol = -1;
            int priceCol = -1;
            if (header != null) {
                for (int i = 0; i < header.getLastCellNum(); i++) {
                    Cell cell = header.getCell(i);
                    if (cell == null) continue;
                    String col = cell.getStringCellValue().trim().toLowerCase();
                    switch (col) {
                        case TITLE_COL -> nameCol = i;
                        case AGE_COL -> ageCol = i;
                        case PRICE_COL -> priceCol = i;
                    }
                }
            }

            List<ProductInfo> list = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String name = getCellString(row, nameCol);
                String age = getCellString(row, ageCol);
                String price = getCellString(row, priceCol);
                list.add(new ProductInfo(i, name, age, price));
            }
            return list;
        }
    }

    private static String getCellString(Row row, int index) {
        if (index < 0) return "";
        Cell cell = row.getCell(index);
        return cell != null ? cell.getStringCellValue() : "";
    }
}
