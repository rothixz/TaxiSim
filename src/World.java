import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

public class World {
    List<Coordinate> stackPos;
    List<TaxiUser> clients;
    List<TaxiUser> availableDrivers;
    Hashtable<TaxiUser, TaxiUser> trips;
    File mapFile;
    int[][] mapMatrix;
    static List<Coordinate> emptyPos;
    private int height, width;
    private Window window;

    public int[][] getMapMatrix() {
        return mapMatrix;
    }

    public List<Coordinate> getEmptyPos() {
        return emptyPos;
    }

    public World(String mapFilename, Window window) {
        this.window = window;
        mapFile = new File(mapFilename);
        stackPos = new ArrayList<Coordinate>();
        clients = new ArrayList<TaxiUser>();
        trips = new Hashtable<TaxiUser, TaxiUser>();
        availableDrivers = new ArrayList<TaxiUser>();
    }

    public boolean loadMap() throws IOException {
        height = MapUtils.countFileRows(mapFile);
        width = MapUtils.countFileColumns(mapFile);
        mapMatrix = MapUtils.fileToMatrix(mapFile);
        emptyPos = MapUtils.getEmptyPos(mapMatrix);

        assert (mapMatrix != null && emptyPos != null && height > 0 && width > 0 && !emptyPos.isEmpty());

        return true;
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
            availableDrivers.add(o);
        }

        assert (o.getPos() != null);
    }

    public TaxiUser callDriver(TaxiUser c) {
        assert !getAvailableDrivers().isEmpty();

        TaxiUser d = getClosestDriver(c);
        availableDrivers.remove(d);
        clients.remove(c);
        trips.put(d, c);

        assert trips.contains(d);

        return d;
    }

    public void waitDriver(TaxiUser c, TaxiUser d) {
        assert isSamePos(c, d);
    }

    public TaxiUser waitClient(TaxiUser d) {
        assert trips.containsKey(d);

        return trips.get(d);
    }

    public void move(TaxiUser o){
        Coordinate newPos = o.getItinerary().remove(1);
        moveInMap(o, newPos);
        printAnsiMap();
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

    public void pickUp(TaxiUser c) {
        Coordinate pos = c.getPos();
        mapMatrix[pos.getY()][pos.getX()] = 3;
        stackPos.remove(pos);
    }


    public List<Coordinate> getItenerary(TaxiUser d, TaxiUser c) {
        List<Coordinate> itinerary = MapUtils.getShortestItenerary(this, c.getPos(), d.getPos());

        return itinerary;
    }

    private TaxiUser getClosestDriver(TaxiUser c) {
        assert (c.getPos() != null);

        double minDuration = Integer.MAX_VALUE;
        TaxiUser d = null;

        for (TaxiUser obj : availableDrivers) {
            double eta = MapUtils.getIteneraryDuration(MapUtils.getShortestItenerary(this, c.getPos(), obj.getPos()));
            if (eta < minDuration) {
                minDuration = eta;
                d = obj;
            }
        }

        assert (d != null);

        return d;
    }

    public List<TaxiUser> getAvailableDrivers() {
        return availableDrivers;
    }

    public Hashtable<TaxiUser, TaxiUser> getTrips() {
        return trips;
    }

    public boolean isSamePos(Coordinate d, Coordinate c) {
        return d.getX() == c.getX() && d.getY() == c.getY();
    }


}
