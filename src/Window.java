import javax.swing.*;
import java.awt.*;
import java.awt.Font;
import javax.swing.JEditorPane;

public class Window extends JFrame {
    private JTextPane countLabel1 = new JTextPane();

    public Window(String title) {
        super(title);
        Font smallFont = new Font("Monospaced", Font.PLAIN, 30);

        countLabel1.setFont(smallFont);
        add(countLabel1);

        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setVisible(true);
    }

    public void updateGUI(String map) {
        countLabel1.setText(map);
    }
}
