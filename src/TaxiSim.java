import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;

/*
    This is the Taxi simulation, it adds Clients and drivers to the World
 */
public class TaxiSim {
    public static void main(String[] args) throws IOException, InterruptedException {
        int numberTaxis = 2;

        if (args.length != 1) {
            System.out.println("Insert the map filename in the arguments.");
            System.exit(1);
        }

        Window window = new Window("Taxi Simulation");
        WorldInterface map = new World(args[0], window);
        map.loadMap();

        SharedWorld world = new SharedWorld(map);

        ExecutorService executor = Executors.newFixedThreadPool(4);

        for (int i = 0; i <= numberTaxis; i++) {
            executor.submit((new Client(world)));
            Thread.sleep((long) ThreadLocalRandom.current().nextInt(0, 1000 + 1));
            executor.submit((new Driver(world)));
            Thread.sleep((long) ThreadLocalRandom.current().nextInt(0, 1000 + 1));
            executor.submit((new Client(world)));
        }
    }
}
