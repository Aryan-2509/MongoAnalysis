package com.example.demoapi;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileOutputStream;
import java.util.List;

public class ExcelWriter {

    public void WriteDocumentSize(int input){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Output");

        String[] headers = {"Document size (bytes)"};
        Row row = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = row.createCell(i);
            headerCell.setCellValue(headers[i]);
        }

        row = sheet.createRow(1);
        Cell cell = row.createCell(0);
        cell.setCellValue(input);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void WriteFieldOverhead(int input){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Output");

        String[] headers = {"Overhead (bytes)"};
        Row row = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = row.createCell(i);
            headerCell.setCellValue(headers[i]);
        }

        row = sheet.createRow(1);
        Cell cell = row.createCell(0);
        cell.setCellValue(input);

        try {
            FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
            workbook.write(fileOutputStream);
            fileOutputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
