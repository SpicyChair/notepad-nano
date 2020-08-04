package editor;

import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.MatchResult;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.filechooser.FileNameExtensionFilter;

public class TextEditor {

    JFrame frame;

    //scrollable text area
    JTextArea textArea;
    JScrollPane scrollableTextArea;


    JPanel topPanel;

    JPanel filePanel;
    JButton saveButton;
    JButton openButton;

    JPanel searchPanel;
    JTextField searchBar;
    JButton searchButton;
    JButton nextMatchButton;
    JButton previousMatchButton;
    JCheckBox toggleRegex;


    JMenuBar menuBar;

    JMenu fileMenu;
    JMenuItem menuSaveButton;
    JMenuItem menuOpenButton;
    JMenuItem menuExitButton;

    JMenu searchMenu;
    JMenuItem menuSearchButton;
    JMenuItem menuNextMatch;
    JMenuItem menuPreviousMatch;
    JMenuItem menuUseRegex;


    JFileChooser chooser;

    ArrayList<MatchResult> searchResults;
    int currentPosition;

    public TextEditor() {
        createJFrame();
        addScrollableTextArea();

        addTopPanel();
        addFilePanelAndButtons();
        addMenuBarWithFileAndSearch();
        addSearchBarAndButtons();

        chooser = new JFileChooser();
        chooser.setName("FileChooser");
        chooser.addChoosableFileFilter(new FileNameExtensionFilter("Text files", "txt", "csv", "json"));
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setVisible(false);

        frame.add(chooser, BorderLayout.PAGE_END);
        
        frame.setVisible(true);

        searchResults = new ArrayList<>();
        int currentPosition = 0;

        setComponentNames();
    }

    public void createJFrame() {
        frame = new JFrame();
        frame.setTitle("Text Editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 600);
    }

    public void addScrollableTextArea() {
        textArea = new JTextArea(50, 50);
        textArea.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));

        scrollableTextArea = new JScrollPane(textArea);
        scrollableTextArea.setBorder(BorderFactory.createEmptyBorder(5, 15, 15, 15));

        frame.add(scrollableTextArea, BorderLayout.CENTER);
    }

    public void addTopPanel() {
        topPanel = new JPanel();
        topPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topPanel.setLayout(new BorderLayout());

        frame.add(topPanel, BorderLayout.NORTH);
    }

    public void addFilePanelAndButtons() {
        filePanel = new JPanel();
        filePanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        filePanel.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        //write to file in filenameField button
        saveButton = new JButton(new ImageIcon("icons/save.png"));
        saveButton.setPreferredSize(new Dimension(40, 40));
        saveButton.setToolTipText("Save as...");
        saveButton.addActionListener(actionEvent -> showSaveDialog());
        filePanel.add(saveButton);

        //load the file in filenameField
        openButton = new JButton(new ImageIcon("icons/open.png"));
        openButton.setPreferredSize(new Dimension(40, 40));
        openButton.setToolTipText("Open file...");
        openButton.addActionListener(actionEvent -> showOpenDialog());
        filePanel.add(openButton);
        
        topPanel.add(filePanel, BorderLayout.WEST);
    }
    
    public void addMenuBarWithFileAndSearch() {
        menuBar = new JMenuBar();
        frame.setJMenuBar(menuBar);
        
        fileMenu = new JMenu("File");
        fileMenu.setMnemonic(KeyEvent.VK_F);

        menuSaveButton = new JMenuItem("Save");
        menuSaveButton.addActionListener(actionEvent -> showSaveDialog());
        fileMenu.add(menuSaveButton);

        menuOpenButton = new JMenuItem("Load");
        menuOpenButton.addActionListener(actionEvent -> showOpenDialog());
        fileMenu.add(menuOpenButton);

        menuExitButton = new JMenuItem("Exit");
        menuExitButton.addActionListener(actionEvent -> frame.dispose());
        fileMenu.add(menuExitButton);
        
        menuBar.add(fileMenu);


        searchMenu = new JMenu("Search");

        menuSearchButton = new JMenuItem("Search");
        menuSearchButton.addActionListener(actionEvent -> startSearch(toggleRegex.isSelected(), searchBar.getText(), textArea.getText()));
        searchMenu.add(menuSearchButton);

        menuNextMatch = new JMenuItem("Next match");
        menuNextMatch.addActionListener(actionEvent -> getNextMatch());
        searchMenu.add(menuNextMatch);

        menuPreviousMatch = new JMenuItem("Previous match");
        menuPreviousMatch.addActionListener(actionEvent -> getPreviousMatch());
        searchMenu.add(menuPreviousMatch);

        menuUseRegex = new JMenuItem("Use regex");
        menuUseRegex.addActionListener(actionEvent -> toggleRegex.setSelected(!toggleRegex.isSelected()));
        searchMenu.add(menuUseRegex);
        
        menuBar.add(searchMenu);
    }

    public void addSearchBarAndButtons() {
        searchPanel = new JPanel();
        searchPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        searchPanel.applyComponentOrientation(ComponentOrientation.LEFT_TO_RIGHT);

        searchBar = new JTextField();
        searchBar.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
        searchBar.setPreferredSize(new Dimension(200,35));
        searchBar.setToolTipText("Type here...");
        searchPanel.add(searchBar);
        
        searchButton = new JButton(new ImageIcon("icons/search.png"));
        searchButton.setPreferredSize(new Dimension(60, 35));
        searchButton.setToolTipText("Search");
        searchButton.addActionListener(actionEvent -> startSearch(toggleRegex.isSelected(), searchBar.getText(), textArea.getText()));
        searchPanel.add(searchButton);

        previousMatchButton = new JButton((new ImageIcon("icons/previous.png")));
        previousMatchButton.setPreferredSize(new Dimension(35, 35));
        previousMatchButton.setToolTipText("Previous result");
        previousMatchButton.addActionListener(actionEvent -> getPreviousMatch());
        searchPanel.add(previousMatchButton);

        nextMatchButton = new JButton((new ImageIcon("icons/next.png")));
        nextMatchButton.setPreferredSize(new Dimension(35, 35));
        nextMatchButton.setToolTipText("Next Result");
        nextMatchButton.addActionListener(actionEvent -> getNextMatch());
        searchPanel.add(nextMatchButton);

        toggleRegex = new JCheckBox();
        searchPanel.add(new JLabel("Use Regex"));
        searchPanel.add(toggleRegex);

        topPanel.add(searchPanel, BorderLayout.EAST);
        
    }

    public void showSaveDialog() {
        chooser.setVisible(true);
        int returnValue = chooser.showSaveDialog(null);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try (FileWriter fw = new FileWriter(chooser.getSelectedFile())){
                fw.write(textArea.getText());
            } catch (Exception ignored) {} 
        }
        chooser.setVisible(false);
    }

    public void showOpenDialog() {
        chooser.setVisible(true);
        textArea.setText(null);
        int returnValue = chooser.showOpenDialog(null);
        
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            try {
                textArea.setText(new String(Files.readAllBytes(Paths.get(chooser.getSelectedFile().getAbsolutePath()))));
            } catch (Exception ignored) {}
        
        }
        chooser.setVisible(false);
    }

    public void startSearch(boolean useRegex, String searchFor, String fileText) {
        SwingWorker<ArrayList<MatchResult>, Object> searcher = new SwingWorker<ArrayList<MatchResult>, Object>() {

			@Override
			protected ArrayList<MatchResult> doInBackground() throws Exception {
                ArrayList<MatchResult> matches = new ArrayList<>();

                Pattern pattern = Pattern.compile(searchFor, useRegex ? Pattern.CASE_INSENSITIVE : Pattern.LITERAL);
                Matcher matcher = pattern.matcher(fileText);

                while (matcher.find()) {
                    matches.add(matcher.toMatchResult());
                }
                return matches;
            }
            
            protected void done() {

                try {
                    searchResults.clear();
                    searchResults = get();
                    currentPosition = 0;

                    int index = searchResults.get(currentPosition).start();
                    String foundText = searchResults.get(currentPosition).group();
    
                    textArea.setCaretPosition(index + foundText.length());
                    textArea.select(index, index + foundText.length());
                    textArea.grabFocus();

                } catch (Exception ignored) {}
            }

        };
        searcher.execute();
    }

    public void getNextMatch() {
        try {
            if (!searchResults.isEmpty()) {

                if (currentPosition == searchResults.size() - 1) {
                    currentPosition = 0;
                } else {
                    currentPosition++;
                }
                
                int index = searchResults.get(currentPosition).start();
                String foundText = searchResults.get(currentPosition).group();

                textArea.setCaretPosition(index + foundText.length());
                textArea.select(index, index + foundText.length());
                textArea.grabFocus();

            }
        } catch (Exception ignored) {}
    }

    public void getPreviousMatch() {
        try {
            if (!searchResults.isEmpty()) {

                if (currentPosition == 0) {
                    currentPosition = searchResults.size() - 1;
                } else {
                    currentPosition--;
                }
                
                int index = searchResults.get(currentPosition).start();
                String foundText = searchResults.get(currentPosition).group();

                textArea.setCaretPosition(index + foundText.length());
                textArea.select(index, index + foundText.length());
                textArea.grabFocus();
            }
        } catch (Exception ignored) {}
    }

    public void setComponentNames() {
        //frame.setName();


        textArea.setName("TextArea");
        scrollableTextArea.setName("ScrollPane");


        saveButton.setName("SaveButton");
        openButton.setName("OpenButton");

        searchBar.setName("SearchField");
        searchButton.setName("StartSearchButton");
        nextMatchButton.setName("NextMatchButton");
        previousMatchButton.setName("PreviousMatchButton");
        toggleRegex.setName("UseRegExCheckbox");

        fileMenu.setName("MenuFile");
        menuSaveButton.setName( "MenuSave");
        menuOpenButton.setName("MenuOpen");
        menuExitButton.setName("MenuExit");
        searchMenu.setName("MenuSearch");
        menuSearchButton.setName("MenuStartSearch");
        menuNextMatch.setName("MenuNextMatch");
        menuPreviousMatch.setName("MenuPreviousMatch");
        menuUseRegex.setName("MenuUseRegExp");


        chooser.setName("FileChooser");
    }
}