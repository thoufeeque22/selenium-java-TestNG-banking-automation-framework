// src/full_framework/utils/ExcelReader.java
package full_framework.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelReader {

    private String resourcePath;
    private String sheetName;
    private static final Logger logger = LogManager.getLogger(ExcelReader.class); // Logger for ExcelReader

    public ExcelReader(String resourcePath, String sheetName) {
        this.resourcePath = resourcePath;
        this.sheetName = sheetName;
        logger.debug("ExcelReader initialized for file: " + resourcePath + ", sheet: " + sheetName);
    }

    public Object[][] getTableData() {
        List<Object[]> data = new ArrayList<>();
        Workbook workbook = null;

        logger.info("Attempting to read Excel data from: " + resourcePath + ", sheet: " + sheetName);
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (inputStream == null) {
                logger.fatal("Excel file not found on classpath: " + resourcePath +
                        ". Ensure it's in src/main/resources and the path is correct.");
                throw new RuntimeException("Excel file not found on classpath: " + resourcePath +
                        ". Ensure it's in src/main/resources and the path is correct.");
            }
            workbook = WorkbookFactory.create(inputStream);
            Sheet sheet = workbook.getSheet(sheetName);

            if (sheet == null) {
                logger.fatal("Sheet '" + sheetName + "' not found in Excel file: " + resourcePath);
                throw new IOException("Sheet '" + sheetName + "' not found in Excel file: " + resourcePath);
            }

            logger.info("Reading data from sheet: " + sheetName);
            int firstRow = sheet.getFirstRowNum() + 1; // Skip header
            int lastRow = sheet.getLastRowNum();

            if (lastRow < firstRow) {
                logger.warn("Excel sheet '" + sheetName + "' has no data rows (only header or empty).");
            }

            for (int i = firstRow; i <= lastRow; i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    logger.warn("Skipping empty row at index: " + i);
                    continue;
                }

                List<Object> rowData = new ArrayList<>();
                int firstCell = row.getFirstCellNum();
                int lastCell = row.getLastCellNum(); // Gets the last column with data + 1

                for (int j = firstCell; j < lastCell; j++) {
                    Cell cell = row.getCell(j, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
                    DataFormatter formatter = new DataFormatter();
                    rowData.add(formatter.formatCellValue(cell));
                }
                data.add(rowData.toArray(new Object[0]));
                logger.debug("Read row " + i + ": " + rowData);
            }
            logger.info("Successfully read " + data.size() + " data rows from Excel.");

        } catch (IOException e) {
            logger.fatal("Error reading Excel file: " + e.getMessage(), e);
            throw new RuntimeException("Failed to read Excel data from " + resourcePath, e);
        } catch (Exception e) {
            logger.fatal("Error processing Excel file: " + e.getMessage(), e);
            throw new RuntimeException("Error processing Excel file: " + resourcePath, e);
        } finally {
            try {
                if (workbook != null) workbook.close();
            } catch (IOException e) {
                logger.error("Error closing Excel workbook: " + e.getMessage(), e);
            }
        }
        return data.toArray(new Object[0][0]);
    }
}