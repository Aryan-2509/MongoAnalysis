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

    public void WriteIndex(List<Index> allIndexes){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Output");

        String[] headers = {"Index Name", "Index Size (bytes)", "Sparse"};
        Row row = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = row.createCell(i);
            headerCell.setCellValue(headers[i]);
        }


        int i = 1;

        for(Index index : allIndexes){
            String indexName = index.name;
            int indexSize = index.size;
            boolean sparse = index.sparse;
            row = sheet.createRow(i);
            Cell cell = row.createCell(0);
            cell.setCellValue(indexName);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            cell = row.createCell(1);
            cell.setCellValue(indexSize);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            cell = row.createCell(2);
            cell.setCellValue(sparse);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            i++;
        }
    }

    public void WriteFaultyIndex(List<FaultyIndex> allIndexes){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Output");

        String[] headers = {"Index Name", "Current Index Size (bytes)", "Ideal Index Size (bytes)"};
        Row row = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = row.createCell(i);
            headerCell.setCellValue(headers[i]);
        }

        int i = 1;

        for(FaultyIndex faultyIndex : allIndexes){

            String indexName = faultyIndex.name;
            int currentIndexSize = faultyIndex.currentSize;
            int idealIndexSize = faultyIndex.idealSize;

            row = sheet.createRow(i);

            Cell cell = row.createCell(0);
            cell.setCellValue(indexName);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            cell = row.createCell(1);
            cell.setCellValue(currentIndexSize);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            cell = row.createCell(2);
            cell.setCellValue(idealIndexSize);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            i++;
        }
    }

    public void WriteIndexOverhead(List<IndexOverhead> allIndexes){
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Output");

        String[] headers = {"Index Name", "Overhead (bytes)"};
        Row row = sheet.createRow(0);

        for (int i = 0; i < headers.length; i++) {
            Cell headerCell = row.createCell(i);
            headerCell.setCellValue(headers[i]);
        }

        int i = 1;

        for(IndexOverhead indexOverhead : allIndexes){

            String indexName = indexOverhead.name;
            int overhead = indexOverhead.overhead;

            row = sheet.createRow(i);

            Cell cell = row.createCell(0);
            cell.setCellValue(indexName);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            cell = row.createCell(1);
            cell.setCellValue(overhead);

            try {
                FileOutputStream fileOutputStream = new FileOutputStream("output.xlsx");
                workbook.write(fileOutputStream);
                fileOutputStream.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

            i++;
        }
    }
}

