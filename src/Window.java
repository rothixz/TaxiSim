import javax.swing.*;
import java.awt.*;

/*
    This is the swing windows which visually represents the world map
 */
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
