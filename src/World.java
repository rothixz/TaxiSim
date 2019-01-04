import java.io.*;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class World {
    File mapFile;
    int [][] mapMatrix;
    List<Coordinate> emptyPos;
    List<Client> clients;
    List<Driver> availableDrivers;
    Map<Driver, Client> trips;
    private int readyToDraw;
    private int height, width;
    private boolean allMoved = false;

    public World(String mapFilename) {
        mapFile = new File(mapFilename);
        clients = new ArrayList<Client>();
        trips = new Hashtable<>();
        availableDrivers =  new ArrayList<Driver>();
        readyToDraw = 0;
    }

    public boolean loadMap() throws IOException {
        height = MapUtils.countFileRows(mapFile);
        width = MapUtils.countFileColumns(mapFile);
        mapMatrix = MapUtils.fileToMatrix(mapFile);
        emptyPos = MapUtils.getEmptyPos(mapMatrix);
        return true;
    }

    public void printAnsiMap(){
        StringBuilder sb = new StringBuilder();

        for(int y=0; y<height; y++){
            for(int x=0; x<width; x++){
                if(mapMatrix[y][x] == 1)
                    sb.append(" ");
                else if(mapMatrix[y][x] == 0)
                    sb.append("@");
                else if(mapMatrix[y][x] == 2)
                    sb.append("C");
                else if(mapMatrix[y][x] == 3)
                    sb.append("T");
            }
            sb.append("\n");
        }

        System.out.print(sb.toString() + "\n");
    }

    public synchronized Driver callDriver(Client c){
        mapMatrix[c.getPos().getY()][c.getPos().getX()] = 2;
        printAnsiMap();

        clients.add(c);

        notifyAll();

        while(availableDrivers.isEmpty()){
            try {
                //System.out.println("CallDriver wait");
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("CallDriver gonna call");
        Driver d = getClosestDriver(c);
        availableDrivers.remove(d);
        trips.put(d, c);
        notifyAll();

        return d;
    }

    public synchronized void waitDriver(Client c, Driver d){
        while(d.getPos().getX() != c.getPos().getX() && d.getPos().getY() != c.getPos().getY()){
            try {
                //System.out.println("Waiting for the driver to pick me up [C]" + Thread.currentThread().getId());

                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("Driver picked me up [C]" + Thread.currentThread().getId());
    }

    public synchronized Client waitClient(Driver d){
        mapMatrix[d.getPos().getY()][d.getPos().getX()] = 3;
        printAnsiMap();
        availableDrivers.add(d);
        notifyAll();

        while(!trips.containsKey(d)){
            try {
                //System.out.println("Waiting for a client to choose me [D]" + Thread.currentThread().getId());
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("A client chose me [D]" + Thread.currentThread().getId());

        return trips.get(d);
    }

    private boolean reachedClient(Driver d, Client c){
        //System.out.println("Driver in pos: (" + d.getPos().getX() + ", " + d.getPos().getY() + ") and client in pos (" + c.getPos().getX() + ", " + c.getPos().getY() +")");
        return d.getPos().getX() == c.getPos().getX() && d.getPos().getY() == c.getPos().getY();

    }

    public synchronized void drawMap(){
        while(!allDriversMoved()){
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        allMoved = true;
        notifyAll();
    }

    private boolean allDriversMoved(){
        for(Driver d: trips.keySet()){
            if(d.getPrevPos() == d.getPos()){
                return false;
            }
        }

        return true;
    }

    public synchronized void driveToClient(Driver d, Client c){
        List<Coordinate> itinerary = MapUtils.getShortestItenerary(mapMatrix, c.getPos(),d.getPos());

        try {
            while (!reachedClient(d, c)){
                Coordinate newPos = itinerary.remove(1);
                moveInMap(d, newPos);
                notifyAll();

                while (!allMoved){
                    wait();
                }

                readyToDraw++;

                //System.out.println("I drove one square meter [D]" + Thread.currentThread().getId());

                if(readyToDraw == trips.size()){
                    printAnsiMap();
                    allMoved = false;
                    readyToDraw = 0;

                    notifyAll();
                }

                while (allMoved){
                    wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        trips.remove(d);
        System.out.println("I reached the client [D]" + Thread.currentThread().getId());
    }

    private void moveInMap(Object o, Coordinate to){
        Coordinate from = null;

        if (o instanceof Client) {
            from = ((Client) o).getPos();
            ((Client) o).setPrevPos(from);
            ((Client) o).setPos(to);
            mapMatrix[to.getY()][to.getX()] = 2;

        }
        else if (o instanceof Driver){
            from = ((Driver) o).getPos();
            ((Driver) o).setPrevPos(from);
            ((Driver) o).setPos(to);
            mapMatrix[to.getY()][to.getX()] = 3;
        }

        mapMatrix[from.getY()][from.getX()] = 1;
        emptyPos.add(from);
    }

    private Driver getClosestDriver(Client c){
        double minDuration = Integer.MAX_VALUE;
        Driver d = null;

        for(Driver obj: availableDrivers){
            double eta = MapUtils.getIteneraryDuration(MapUtils.getShortestItenerary(mapMatrix, c.getPos(),obj.getPos()));
            if(eta<minDuration){
                minDuration = eta;
                d = obj;
            }
        }

        return d;
    }

    public Coordinate getEmptyPosition(){
        int randomEmptyPosIdx = ThreadLocalRandom.current().nextInt(0, emptyPos.size());

        return emptyPos.remove(randomEmptyPosIdx);
    }
}