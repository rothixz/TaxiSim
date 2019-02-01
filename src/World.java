import java.io.File;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/*
    This class is used to represent a world of drivers and clients.
    It has a map where the drivers and clients spawn and where drivers can move around picking clients
    and delivering them to their destinations.
 */
public class World implements WorldInterface {
    // List of drivable or spawnable positions
    static List<Coordinate> emptyPos;
    // List of coordinates which have more than one entity present
    List<Coordinate> stackPos;
    // List of clients which are present in the world
    List<TaxiUser> clients;
    // List of drivers which are present in the world
    List<TaxiUser> drivers;
    // List of trips being executed
    Hashtable<TaxiUser, TaxiUser> trips;
    // File with the world map
    File mapFile;
    // 2D Map coordinate matrix representation
    int[][] mapMatrix;
    // Height of the map
    private int height;
    // Width of the map
    private int width;
    // Swing window where the map is visually represented
    private Window window;

    public World(String mapFilename, Window window) {
        this.window = window;
        mapFile = new File(mapFilename);
        stackPos = new ArrayList<Coordinate>();
        clients = new ArrayList<TaxiUser>();
        trips = new Hashtable<TaxiUser, TaxiUser>();
        drivers = new ArrayList<TaxiUser>();
    }

    public int[][] getMapMatrix() {
        return mapMatrix;
    }

    public List<Coordinate> getEmptyPos() {
        return emptyPos;
    }

    public void loadMap() {
        mapMatrix = MapUtils.fileToMatrix(mapFile);
        height = mapMatrix.length;
        width = mapMatrix[0].length;
        emptyPos = MapUtils.getEmptyPos(mapMatrix);

        assert (mapMatrix != null && emptyPos != null && height > 0 && width > 0 && !emptyPos.isEmpty());
    }

    public void printAnsiMap() {
        assert (mapMatrix != null && height > 0 && width > 0);

        StringBuilder sb = new StringBuilder();

        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (mapMatrix[y][x] == 1)
                    sb.append(" ");
                else if (mapMatrix[y][x] == 0)
                    sb.append("\u2588");
                else if (mapMatrix[y][x] == 2)
                    sb.append("C");
                else if (mapMatrix[y][x] == 3)
                    sb.append("T");
            }
            sb.append("\n");
        }

        System.out.println(sb.toString());

        window.updateGUI(sb.toString());
    }

    public void addToMap(TaxiUser o) {
        assert !getEmptyPos().isEmpty();

        int randomEmptyPosIdx = ThreadLocalRandom.current().nextInt(0, emptyPos.size());
        o.setPos(emptyPos.remove(randomEmptyPosIdx));

        if (o instanceof Client) {
            randomEmptyPosIdx = ThreadLocalRandom.current().nextInt(0, emptyPos.size());
            List<Coordinate> destination = new ArrayList<>();
            destination.add(emptyPos.get(randomEmptyPosIdx));
            o.setItinerary(destination);
            mapMatrix[o.getPos().getY()][o.getPos().getX()] = 2;
            clients.add(o);
        } else if (o instanceof Driver) {
            mapMatrix[o.getPos().getY()][o.getPos().getX()] = 3;
            drivers.add(o);
        }

        assert (o.getPos() != null);
    }

    public TaxiUser callDriver(TaxiUser c) {
        assert !getDrivers().isEmpty();

        TaxiUser d = getClosestDriver(c);
        drivers.remove(d);
        clients.remove(c);
        trips.put(d, c);

        assert trips.contains(d);

        return d;
    }

    public void waitDriver(TaxiUser c, TaxiUser d) {
        assert isSamePos(c.getPos(), d.getPos());
    }

    public void waitDelivery(Client c) {
        assert isSamePos(c.getPos(), c.getItinerary().get(0));
    }

    public TaxiUser waitClient(TaxiUser d) {
        assert trips.containsKey(d);

        return trips.get(d);
    }

    public void move(TaxiUser o) {
        Coordinate newPos = o.getItinerary().remove(1);
        moveInMap(o, newPos);

        if (((Driver) o).isDelivering())
            trips.get(o).setPos(newPos);
    }

    public void moveInMap(TaxiUser o, Coordinate to) {
        assert (o.getPos() != null && to != null && !o.getPos().equals(to) && mapMatrix != null && stackPos != null && emptyPos != null);

        if (!emptyPos.contains(to))
            stackPos.add(to);
        else
            emptyPos.remove(to);

        Coordinate from = o.getPos();
        o.setPrevPos(from);
        o.setPos(to);

        if (o instanceof Client)
            mapMatrix[to.getY()][to.getX()] = 2;
        else if (o instanceof Driver)
            mapMatrix[to.getY()][to.getX()] = 3;

        if (!stackPos.contains(from)) {
            mapMatrix[from.getY()][from.getX()] = 1;
            emptyPos.add(from);
        } else {
            stackPos.remove(from);
        }
    }

    public void pickUp(TaxiUser d, TaxiUser c) {
        Coordinate pos = c.getPos();
        mapMatrix[pos.getY()][pos.getX()] = 3;
        stackPos.remove(pos);
        ((Driver) d).setDelivering(true);
    }

    public void deliver(TaxiUser d, TaxiUser c) {
        Coordinate pos = d.getPos();
        stackPos.remove(pos);
        ((Driver) d).setDelivering(false);
        trips.remove(d);
    }

    public void available(Driver d) {
        drivers.add(d);
    }

    public List<Coordinate> getItenerary(TaxiUser d, Coordinate c) {
        List<Coordinate> itinerary = MapUtils.getShortestItenerary(this, d.getPos(), c);
        return itinerary;
    }

    public TaxiUser getClosestDriver(TaxiUser c) {
        assert (c.getPos() != null);

        double minDuration = Integer.MAX_VALUE;
        TaxiUser d = null;

        for (TaxiUser obj : drivers) {
            double eta = MapUtils.getItineraryDuration(MapUtils.getShortestItenerary(this, obj.getPos(), c.getPos()));
            if (eta < minDuration) {
                minDuration = eta;
                d = obj;
            }
        }

        assert (d != null);

        return d;
    }

    public List<TaxiUser> getDrivers() {
        return drivers;
    }

    public Hashtable<TaxiUser, TaxiUser> getTrips() {
        return trips;
    }

    public boolean isSamePos(Coordinate d, Coordinate c) {
        return d.getX() == c.getX() && d.getY() == c.getY();
    }
}
