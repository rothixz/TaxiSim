import java.applet.Applet;
import java.awt.*;
import java.io.File;
import java.io.IOException;

public class Renderer extends Applet {
    static int[][] tilemap;
    static int rows, columns;

    @Override
    public void init() {
        setSize(800, 480);
        setBackground(Color.BLACK);
        File file = new File("4x4map");
        try {
            createTilemap(file);
        } catch (IOException e){
            System.out.print(e.toString());
        }
    }

    @Override
    public void paint(Graphics g) {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                int mod_i = 200*i;
                int mod_j = 120*j;

                switch (tilemap[i][j]) {
                    case 0:
                        g.setColor(Color.BLACK);
                        g.fillRect(mod_i, mod_j, 200, 120);
                        break;
                    case 1:
                        g.setColor(Color.WHITE);
                        g.fillRect(mod_i, mod_j, 200, 120);
                        break;
                }

            }
        }
    }

    private void createTilemap(File file) throws IOException {
        tilemap = MapUtils.fileToMatrix(file);
        columns = MapUtils.countFileColumns(file);
        rows = MapUtils.countFileRows(file);
    }
}