import java.io.IOException;
import java.util.concurrent.ThreadLocalRandom;

public class TaxiSim {
    public static void main(String[] args) throws IOException, InterruptedException {
          final String ANSI_RESET = "\u001B[0m";
          final String ANSI_RED = "\u001B[31m";

        Window window = new Window("teste");
        System.out.println(ANSI_RED + "This text is red!" + ANSI_RESET);

        SharedWorld map = new SharedWorld(args[0], window);
        map.loadMap();

        World world = new World(map);

        Thread r = new MapRenderer(world);
        r.start();

        while (true){
            Thread.sleep((long) ThreadLocalRandom.current().nextInt(0, 3000 + 1));
            new Thread(new Client(world)).start();
            Thread.sleep((long) ThreadLocalRandom.current().nextInt(0, 3000 + 1));
            new Thread(new Driver(world)).start();
        }
    }
}
