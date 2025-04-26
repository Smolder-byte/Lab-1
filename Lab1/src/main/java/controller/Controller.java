package controller;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import javax.swing.*;
import java.awt.event.*;
import java.io.*;
import java.util.List;
import model.*;
import view.GUI;

public class Controller implements ActionListener {
    private GUI view;
    private Data model;
    
    public Controller(GUI view, Data model) {
        this.view = view;
        this.model = model;
        
        // Назначаем обработчики кнопок
        view.getImportButton().addActionListener(this);
        view.getCalculateButton().addActionListener(this);
        view.getExportButton().addActionListener(this);
        view.getExitButton().addActionListener(this);
    }
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getImportButton()) {
            importData();
        } else if (e.getSource() == view.getCalculateButton()) {
            calculateStatistics();
        } else if (e.getSource() == view.getExportButton()) {
            exportResults();
        } else if (e.getSource() == view.getExitButton()) {
            System.exit(0);
        }
    }
    
    private void importData() {
        String path = view.showFileDialog("Выберите файл Excel", JFileChooser.OPEN_DIALOG);
        if (path == null) return;
        
        try (FileInputStream fis = new FileInputStream(path);
             Workbook workbook = new XSSFWorkbook(fis)) {
            
            model.getSamples().clear();
            view.clearText();
            
            Sheet sheet = workbook.getSheetAt(0);
            int colCount = sheet.getRow(0).getLastCellNum();
            
            // Читаем данные по столбцам
            for (int col = 0; col < colCount; col++) {
                int rowCount = sheet.getPhysicalNumberOfRows();
                double[] sample = new double[rowCount - 1]; // Пропускаем заголовок
                
                for (int row = 1; row < rowCount; row++) {
                    Cell cell = sheet.getRow(row).getCell(col);
                    sample[row - 1] = cell.getNumericCellValue();
                }
                
                model.addSample(sample);
            }
            
            view.appendText("Успешно загружено " + colCount + " выборок");
        } catch (Exception e) {
            view.showError("Ошибка при импорте: " + e.getMessage());
        }
    }
    
    private void calculateStatistics() {
        if (model.getSamples().isEmpty()) {
            view.showError("Сначала загрузите данные!");
            return;
        }
        
        model.clearResults();
        view.clearText();
        
        for (int i = 0; i < model.getSamples().size(); i++) {
            double[] sample = model.getSamples().get(i);
            
            // Вычисляем все показатели
            model.addResult("GeometricMean", Calculator.geometricMean(sample));
            model.addResult("ArithmeticMean", Calculator.arithmeticMean(sample));
            model.addResult("StandardDeviation", Calculator.standardDeviation(sample));
            model.addResult("Range", Calculator.range(sample));
            model.addResult("Count", Calculator.count(sample));
            model.addResult("VariationCoefficient", Calculator.variationCoefficient(sample));
            
            double[] ci = Calculator.confidenceInterval(sample);
            model.addResult("ConfidenceLower", ci[0]);
            model.addResult("ConfidenceUpper", ci[1]);
            
            model.addResult("Variance", Calculator.variance(sample));
            model.addResult("Min", Calculator.min(sample));
            model.addResult("Max", Calculator.max(sample));
            
            // Выводим результаты
            view.appendText(String.format("\nВыборка %d:", i+1));
            view.appendText(String.format("Элементов: %d", sample.length));
            view.appendText(String.format("Среднее: %.2f", Calculator.arithmeticMean(sample)));
            view.appendText(String.format("Станд. отклонение: %.2f", Calculator.standardDeviation(sample)));
        }
        
        // Ковариация для пар выборок
        if (model.getSamples().size() > 1) {
            view.appendText("\nКовариация:");
            for (int i = 0; i < model.getSamples().size(); i++) {
                for (int j = i+1; j < model.getSamples().size(); j++) {
                    double cov = Calculator.covariance(
                        model.getSamples().get(i), 
                        model.getSamples().get(j)
                    );
                    view.appendText(String.format("Выборки %d и %d: %.4f", i+1, j+1, cov));
                }
            }
        }
    }
    
    private void exportResults() {
        String path = view.showFileDialog("Сохранить результаты", JFileChooser.SAVE_DIALOG);
        if (path == null) return;
        
        if (!path.endsWith(".xlsx")) path += ".xlsx";
        
        try (Workbook workbook = new XSSFWorkbook();
             FileOutputStream fos = new FileOutputStream(path)) {
            
            Sheet sheet = workbook.createSheet("Результаты");
            
            // Заголовки
            Row header = sheet.createRow(0);
            header.createCell(0).setCellValue("Показатель");
            for (int i = 0; i < model.getSamples().size(); i++) {
                header.createCell(i+1).setCellValue("Выборка " + (i+1));
            }
            
            // Данные
            addMetricRow(sheet, 1, "Среднее арифметическое", "ArithmeticMean");
            addMetricRow(sheet, 2, "Стандартное отклонение", "StandardDeviation");
            addMetricRow(sheet, 3, "Размах", "Range");
            addMetricRow(sheet, 4, "Коэфф. вариации", "VariationCoefficient");
            addMetricRow(sheet, 5, "Дов. интервал (нижн)", "ConfidenceLower");
            addMetricRow(sheet, 6, "Дов. интервал (верхн)", "ConfidenceUpper");
            addMetricRow(sheet, 7, "Минимум", "Min");
            addMetricRow(sheet, 8, "Максимум", "Max");
            
            workbook.write(fos);
            view.appendText("\nРезультаты сохранены в: " + path);
        } catch (Exception e) {
            view.showError("Ошибка при экспорте: " + e.getMessage());
        }
    }
    
    private void addMetricRow(Sheet sheet, int rowNum, String name, String metric) {
        Row row = sheet.createRow(rowNum);
        row.createCell(0).setCellValue(name);
        
        List<Double> values = model.getResults().get(metric);
        for (int i = 0; i < values.size(); i++) {
            row.createCell(i+1).setCellValue(values.get(i));
        }
    }
}