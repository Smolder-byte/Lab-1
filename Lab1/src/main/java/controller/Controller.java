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
    String path = view.showFileDialog("Выберите файл Excel");
    if (path == null) return;
    
    try (FileInputStream fis = new FileInputStream(path);
         Workbook workbook = new XSSFWorkbook(fis)) {
        
        int sheetCount = workbook.getNumberOfSheets();
        if (sheetCount == 0) {
            view.showError("Файл не содержит ни одного листа!");
            return;
        }
        
        String sheetNumberStr = JOptionPane.showInputDialog(
            view.getFrame(),
            "Введите номер листа (1-" + sheetCount + "):", "Выбор листа", JOptionPane.QUESTION_MESSAGE);
        
        if (sheetNumberStr == null) return;
        
        int sheetNumber;
        try {
            sheetNumber = Integer.parseInt(sheetNumberStr) - 1;
            if (sheetNumber < 0 || sheetNumber >= sheetCount) {
                view.showError("Номер листа должен быть от 1 до " + sheetCount);
                return;
            }
        } catch (NumberFormatException e) {
            view.showError("Введите корректный номер листа!");
            return;
        }
        
        model.getSamples().clear();
        view.clearText();
        
        Sheet sheet = workbook.getSheetAt(sheetNumber);
        if (sheet.getPhysicalNumberOfRows() == 0) {
            view.showError("Выбранный лист пуст!");
            return;
        }
        
        int colCount = sheet.getRow(0).getLastCellNum();
        
        for (int col = 0; col < colCount; col++) {
            int rowCount = sheet.getPhysicalNumberOfRows();
            double[] sample = new double[rowCount - 1];
            
            for (int row = 1; row < rowCount; row++) {
                Row currentRow = sheet.getRow(row);
                if (currentRow == null) continue;
                
                Cell cell = currentRow.getCell(col);
                if (cell == null) {
                    sample[row - 1] = 0;
                    continue;
                }
                
                switch (cell.getCellType()) {
                    case NUMERIC:
                        sample[row - 1] = cell.getNumericCellValue();
                        break;
                    case FORMULA:
                        FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
                        CellValue cellValue = evaluator.evaluate(cell);
                        
                        if (cellValue.getCellType() == CellType.NUMERIC) {
                            sample[row - 1] = cellValue.getNumberValue();
                        } else {
                            sample[row - 1] = 0;
                        }
                        break;
                    default:
                        sample[row - 1] = 0;
                }
            }
            
            model.addSample(sample);
        }
        
        view.appendText("Успешно загружено " + colCount + " выборок с листа " + (sheetNumber + 1));
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
        
        view.appendText(String.format("\nВыборка: " + (i+1)));
        view.appendText(String.format("Все показатели рассчитаны"));
    }
    
    if (model.getSamples().size() > 1) {
        view.appendText("\nКовариация:");
        for (int i = 0; i < model.getSamples().size(); i++) {
            for (int j = i+1; j < model.getSamples().size(); j++) {
                double cov = Calculator.covariance(
                    model.getSamples().get(i), 
                    model.getSamples().get(j)
                );
                view.appendText(String.format("Выборки " + (i+1) + " и " + (j+1) + ": " + cov));
            }
        }
    }
}

private void exportResults() {
    String path = view.showFileDialog("Сохранить результаты");
    if (path == null) return;
    
    if (!path.endsWith(".xlsx")) path += ".xlsx";
    
    try (Workbook workbook = new XSSFWorkbook();
         FileOutputStream fos = new FileOutputStream(path)) {
        
        Sheet sheet = workbook.createSheet("Результаты");
        
        Row header = sheet.createRow(0);
        header.createCell(0).setCellValue("Показатель");
        for (int i = 0; i < model.getSamples().size(); i++) {
            header.createCell(i+1).setCellValue("Выборка " + (i+1));
        }
        
        addMetricRow(sheet, 1, "Среднее геометрическое", "GeometricMean");
        addMetricRow(sheet, 2, "Среднее арифметическое", "ArithmeticMean");
        addMetricRow(sheet, 3, "Стандартное отклонение", "StandardDeviation");
        addMetricRow(sheet, 4, "Размах", "Range");
        addMetricRow(sheet, 5, "Количество элементов", "Count");
        addMetricRow(sheet, 6, "Коэфф. вариации", "VariationCoefficient");
        addMetricRow(sheet, 7, "Дов. интервал (нижн)", "ConfidenceLower");
        addMetricRow(sheet, 8, "Дов. интервал (верхн)", "ConfidenceUpper");
        addMetricRow(sheet, 9, "Дисперсия", "Variance");
        addMetricRow(sheet, 10, "Минимум", "Min");
        addMetricRow(sheet, 11, "Максимум", "Max");
        
        if (model.getSamples().size() > 1) {
            int lastRowNum = sheet.getLastRowNum();
            
            Row covarHeader = sheet.createRow(lastRowNum + 2);
            covarHeader.createCell(0).setCellValue("Ковариация");
            
            for (int i = 0; i < model.getSamples().size(); i++) {
                Row row = sheet.createRow(lastRowNum + 3 + i);
                row.createCell(0).setCellValue("Выборка " + (i+1));
                
                for (int j = 0; j < model.getSamples().size(); j++) {
                        double cov = Calculator.covariance(
                            model.getSamples().get(i),
                            model.getSamples().get(j)
                        );
                        row.createCell(j+1).setCellValue(cov);
                    }
                }
            }
        
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