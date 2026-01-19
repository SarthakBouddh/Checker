package com.example.testing.services;

import com.example.testing.dto.DistanceResult;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {

    private boolean isRowEmpty(Row row, DataFormatter formatter) {

        for (int c = 0; c < 3; c++) { // only check Start, End, Distance columns
            Cell cell = row.getCell(c);
            if (cell != null && !formatter.formatCellValue(cell).trim().isEmpty()) {
                return false;
            }
        }
        return true;
    }

    public List<DistanceResult> processExcel(MultipartFile file) throws Exception {

        List<DistanceResult> results = new ArrayList<>();

        Workbook workbook = new XSSFWorkbook(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);

        DataFormatter formatter = new DataFormatter();

        for (int i = 1; i <= sheet.getLastRowNum(); i++) {

            Row row = sheet.getRow(i);
            if (row == null || isRowEmpty(row, formatter)) break;

            int start = readInt(row.getCell(0), formatter);
            int end = readInt(row.getCell(1), formatter);
            int distance = readInt(row.getCell(2), formatter);

            DistanceResult result = validateAndFix(i + 1, start, end, distance);
            results.add(result);
        }

        workbook.close();
        return results;
    }

    // 🔒 SAFE CELL READER
    private int readInt(Cell cell, DataFormatter formatter) {

        if (cell == null) return -1;

        String value = formatter.formatCellValue(cell).trim();

        if (
                value.isEmpty() ||
                        value.equalsIgnoreCase("NA") ||
                        value.equalsIgnoreCase("Image Blurr")
        ) {
            return -1;
        }

        try {
            return Integer.parseInt(value);
        } catch (Exception e) {
            return -1;
        }
    }

    // ✅ CORE BUSINESS LOGIC
    private DistanceResult validateAndFix(int row, int start, int end, int distance) {

        // Both missing
        if (start == -1 && end == -1) {
            return new DistanceResult(row, -1, -1, distance, "INVALID");
        }

        // Start missing → calculate
        if (start == -1) {
            start = end - distance;
            return new DistanceResult(row, start, end, distance, "START_CALCULATED");
        }

        // End missing → calculate
        if (end == -1) {
            end = start + distance;
            return new DistanceResult(row, start, end, distance, "END_CALCULATED");
        }

        // Both present → validate
        if ((end - start) == distance) {
            return new DistanceResult(row, start, end, distance, "VALID");
        } else {
            // Correct end using distance
            end = start + distance;
            return new DistanceResult(row, start, end, distance, "CORRECTED");
        }
    }
}
