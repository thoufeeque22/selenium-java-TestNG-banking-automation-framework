// src/main/java/full_framework/utils/ExcelReader.java
package full_framework.utils;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    private String filePath;
    private String sheetName;

    public ExcelReader(String filePath, String sheetName) {
        this.filePath = filePath;
        this.sheetName = sheetName;
    }

    public Object[][] getTableData() {
        List<Object[]> data = new ArrayList<>();
        FileInputStream fis = null;
        Workbook workbook = null;

        try {
            fis = new FileInputStream(new File(filePath));
            workbook = new XSSFWorkbook(fis); // For .xlsx files
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                throw new IOException("Sheet '" + sheetName + "' not found in Excel file: " + filePath);
            }

            // Skip the header row (row 0)
            int firstRow = sheet.getFirstRowNum() + 1;
            int lastRow = sheet.getLastRowNum();

            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    continue; // Skip empty rows
                }

                List<Object> rowData = new ArrayList<>();
                // Assuming all columns are strings for simplicity
                int firstCell = row.getFirstCellNum();
                int lastCell = row.getLastCellNum(); // Gets the last column with data + 1

                for (int j = firstCell; j < lastCell; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    // Use DataFormatter to read all cell types as String
                    DataFormatter formatter = new DataFormatter();
                    rowData.add(formatter.formatCellValue(cell));
                }
                data.add(rowData.toArray(new Object[0]));
            }

        } catch (IOException e) {
            System.err.println("Error reading Excel file: " + e.getMessage());
            throw new RuntimeException("Failed to read Excel data from " + filePath, e);
        } finally {
            try {
                if (workbook != null) workbook.close();
                if (fis != null) fis.close();
            } catch (IOException e) {
                System.err.println("Error closing Excel resources: " + e.getMessage());
            }
        }
        return data.toArray(new Object[0][0]);
    }
}