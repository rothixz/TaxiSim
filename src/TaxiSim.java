import javax.swing.*;
import java.io.IOException;

public class TaxiSim {
    public static void main(String[] args) throws IOException {
        Window window = new Window("teste");

        World worldMap = new World(args[0], window);
        worldMap.loadMap();

        Thread r = new MapRenderer(worldMap);
        Thread c = new Client(worldMap.removeEmptyPosition(), worldMap);
        Thread d = new Driver(worldMap.removeEmptyPosition(), worldMap);
        Thread c2 = new Client(worldMap.removeEmptyPosition(), worldMap);
        Thread d2 = new Driver(worldMap.removeEmptyPosition(), worldMap);

        r.start();
        c.start();
        d.start();
        c2.start();
        d2.start();
    }
}
