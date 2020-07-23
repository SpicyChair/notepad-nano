package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
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

    JMenuBar menuBar;

    JMenu fileMenu;
    JMenuItem menuSave;
    JMenuItem menuLoad;
    JMenuItem menuExit;


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

        //where user enters name of file
        filenameField = new JTextField(20);
        filenameField.setName("FilenameField");
        controlPanel.add(filenameField);

        //write to file in filenameField button
        saveButton = new JButton("Save");
        saveButton.addActionListener(actionEvent -> {

            try (PrintWriter printWriter = new PrintWriter(new File(filenameField.getText()))) {
                printWriter.print(textArea.getText());
            } catch (Exception ignored) {}
        });
        saveButton.setName("SaveButton");
        controlPanel.add(saveButton);

        //load the file in filenameField
        loadButton = new JButton("Load");
        loadButton.addActionListener(actionEvent -> {
            textArea.setText(null);
            try {
                textArea.setText(new String(Files.readAllBytes(Paths.get(filenameField.getText()))));
            } catch (Exception ignored) {}
        });
        loadButton.setName("LoadButton");
        controlPanel.add(loadButton);

        //control panel added to JFrame
        add(controlPanel, BorderLayout.NORTH);

        menuBar = new JMenuBar();
        setJMenuBar(menuBar);
        
        fileMenu = new JMenu("File");
        fileMenu.setName("MenuFile");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        menuSave = new JMenuItem("Save");
        menuSave.setName("MenuSave");
        menuSave.addActionListener(actionEvent -> {

            try (PrintWriter printWriter = new PrintWriter(new File(filenameField.getText()))) {
                printWriter.print(textArea.getText());
            } catch (Exception ignored) {}
        });
        fileMenu.add(menuSave);


        menuLoad = new JMenuItem("Load");
        menuLoad.setName("MenuLoad");
        menuLoad.addActionListener(actionEvent -> {
            textArea.setText(null);
            try {
                textArea.setText(new String(Files.readAllBytes(Paths.get(filenameField.getText()))));
            } catch (Exception ignored) {}
        });
        fileMenu.add(menuLoad);

        menuExit = new JMenuItem("Exit");
        menuExit.setName("MenuExit");
        menuExit.addActionListener(actionEvent -> dispose());
        fileMenu.add(menuExit);
        
        menuBar.add(fileMenu);


        setVisible(true);
    }
}