package utils;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import testDataObject.LeapFrog.ProductInfo;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {
    public static List<ProductInfo> readProducts(String path) throws IOException {
        try (FileInputStream fis = new FileInputStream(path);
             XSSFWorkbook wb = new XSSFWorkbook(fis)) {
            Sheet sheet = wb.getSheetAt(0);
            List<ProductInfo> list = new ArrayList<>();
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                String name = row.getCell(0).getStringCellValue();
                String age = row.getCell(1).getStringCellValue();
                String price = row.getCell(2).getStringCellValue();
                list.add(new ProductInfo(i, name, age, price));
            }
            return list;
        }
    }
}
