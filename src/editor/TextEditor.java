package editor;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

public class TextEditor extends JFrame {


    private static final long serialVersionUID = 1L;
    
    JTextArea textArea;
    JScrollPane scrollableTextArea;

    JPanel controlPanel;
    JTextField filenameField;
    JButton saveButton;
    JButton loadButton;

    //String filename;

    public TextEditor() {
        setTitle("Text Editor");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 600);
        setVisible(true);

        // Initialise text input area and scroll pane
        textArea = new JTextArea(50, 50);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        textArea.setName("TextArea");

        scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));
        scrollableTextArea.setName("ScrollPane");

        add(scrollableTextArea, BorderLayout.CENTER);

        // Initialise control panel for saving/loading files
        controlPanel = new JPanel();
        controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
        controlPanel.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        filenameField = new JTextField(20);
        filenameField.setName("FilenameField");
        controlPanel.add(filenameField);

        saveButton = new JButton("Save");
        saveButton.addActionListener(actionEvent -> {

            try (PrintWriter printWriter = new PrintWriter(new File(filenameField.getText()))) {
                printWriter.print(textArea.getText());
            } catch (Exception ignored) {
            }
        });
        saveButton.setName("SaveButton");
        controlPanel.add(saveButton);

        loadButton = new JButton("Load");
        loadButton.addActionListener(actionEvent -> {
            textArea.setText(null);
            try {
                textArea.setText(new String(Files.readAllBytes(Paths.get(filenameField.getText()))));
            } catch (Exception e) {
                System.out.println("Cannot read file: " + e.getMessage());
            }
        });
        loadButton.setName("LoadButton");
        controlPanel.add(loadButton);

        add(controlPanel, BorderLayout.NORTH);

        setVisible(true);
    }
}