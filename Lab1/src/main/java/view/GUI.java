package view;

import javax.swing.*;
import java.awt.*;

public class GUI {
    private JFrame frame;
    private JTextArea resultArea;
    private JButton importButton;
    private JButton calculateButton;
    private JButton exportButton;
    private JButton exitButton;

    public GUI() {
        // Настройка основного окна
        frame = new JFrame("Анализатор данных");
        frame.setSize(600, 400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Создаем кнопки
        importButton = new JButton("Импорт");
        calculateButton = new JButton("Рассчитать");
        exportButton = new JButton("Экспорт");
        exitButton = new JButton("Выход");
        resultArea = new JTextArea();

        // Размещаем компоненты
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(importButton);
        buttonPanel.add(calculateButton);
        buttonPanel.add(exportButton);
        buttonPanel.add(exitButton);

        frame.add(buttonPanel, BorderLayout.NORTH);
        frame.add(new JScrollPane(resultArea), BorderLayout.CENTER);
        frame.setVisible(true);
    }

    // Методы для доступа к компонентам
    public JButton getImportButton() { return importButton; }
    public JButton getCalculateButton() { return calculateButton; }
    public JButton getExportButton() { return exportButton; }
    public JButton getExitButton() { return exitButton; }

    // Методы для работы с текстовой областью
    public void appendText(String text) {
        resultArea.append(text + "\n");
    }
    
    public void clearText() {
        resultArea.setText("");
    }

    // Методы для диалоговых окон
    public void showError(String message) {
        JOptionPane.showMessageDialog(frame, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
    
    public String showFileDialog(String title, int mode) {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle(title);
        if (chooser.showDialog(frame, mode == JFileChooser.OPEN_DIALOG ? "Открыть" : "Сохранить") == JFileChooser.APPROVE_OPTION) {
            return chooser.getSelectedFile().getPath();
        }
        return null;
    }
}