package editor;

import javax.swing.SwingUtilities;

public class ApplicationRunner {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(
                () -> new TextEditor()
        );
    }    
}
