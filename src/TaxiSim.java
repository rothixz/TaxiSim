import java.io.IOException;

public class TaxiSim {
    public static void main(String[] args) throws IOException {
        World worldMap = new World(args[0]);
        worldMap.loadMap();

        Thread r = new MapRenderer(worldMap);
        Thread c = new Client(worldMap.getEmptyPosition(), worldMap);
        Thread d = new Driver(worldMap.getEmptyPosition(), worldMap);
        Thread c2 = new Client(worldMap.getEmptyPosition(), worldMap);
        Thread d2 = new Driver(worldMap.getEmptyPosition(), worldMap);

        r.start();
        c.start();
        d.start();
        c2.start();
        d2.start();
    }
}
