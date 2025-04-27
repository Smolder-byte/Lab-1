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
    frame = new JFrame("Рассчёт показателей");
    frame.setSize(600, 400);
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    importButton = new JButton("Импорт");
    calculateButton = new JButton("Рассчитать");
    exportButton = new JButton("Экспорт");
    exitButton = new JButton("Выход");
    resultArea = new JTextArea();

    JPanel buttonPanel = new JPanel();
    buttonPanel.add(importButton);
    buttonPanel.add(calculateButton);
    buttonPanel.add(exportButton);
    buttonPanel.add(exitButton);

    frame.add(buttonPanel, BorderLayout.NORTH);
    frame.add(new JScrollPane(resultArea), BorderLayout.CENTER);
    frame.setVisible(true);
}

public JButton getImportButton() { 
    return importButton; 
}
public JButton getCalculateButton() {
    return calculateButton; 
}
public JButton getExportButton() {
    return exportButton;
}
public JButton getExitButton() {
    return exitButton; 
}

public JFrame getFrame() {
    return frame;
}

public void appendText(String text) {
    resultArea.append(text + "\n");
}

public void clearText() {
    resultArea.setText("");
}

public void showError(String message) {
    JOptionPane.showMessageDialog(frame, message, "Ошибка", JOptionPane.ERROR_MESSAGE);
}

public String showFileDialog(String title) {
    JFileChooser chooser = new JFileChooser();
    chooser.setDialogTitle(title);
    int result = chooser.showOpenDialog(frame);
    if (result == JFileChooser.APPROVE_OPTION) {
        return chooser.getSelectedFile().getPath();
    }
    return null;
}
}