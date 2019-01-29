import java.util.*;
import java.util.concurrent.Semaphore;

public class SharedWorld {
    World map;
    Semaphore cli = new Semaphore(2);
    Semaphore dri = new Semaphore(1);
    private int readyToDraw;
    private boolean allMoved = false;

    public SharedWorld(World map) {
        this.map = map;
        readyToDraw = 0;
    }

    public synchronized void addToMap(TaxiUser o){
        while (map.getEmptyPos().isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            if(o instanceof Client){
                cli.acquire();
            } else if(o instanceof Driver){
                dri.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        map.addToMap(o);

        notifyAll();
    }

    public TaxiUser callDriver(TaxiUser c) {
        synchronized (this) {
            while (map.getAvailableDrivers().isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            TaxiUser d = map.callDriver(c);
            notifyAll();

            return d;
        }
    }

    public void waitDriver(TaxiUser c, TaxiUser d) {
        synchronized (this) {
            while (!map.isSamePos(c, d)) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            map.waitDriver(c, d);
        }
    }

    public synchronized TaxiUser waitClient(TaxiUser d) {
        while (!map.getTrips().containsKey(d)) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        TaxiUser c = map.waitClient(d);

        return c;
    }

    public synchronized void driveToClient(TaxiUser d, TaxiUser c) {
        assert (d.getPos() != null && c.getPos() != null);

        List<Coordinate> itinerary = MapUtils.getShortestItenerary(map, c.getPos(), d.getPos());

        assert (itinerary.size() > 1);

        try {
                Coordinate newPos = itinerary.remove(1);
                map.moveInMap(d, newPos);
                notifyAll();

                while (!allMoved) {
                    wait();
                }

                readyToDraw++;

                //System.out.println("I drove one square meter [D]" + Thread.currentThread().getId());

                if (readyToDraw == trips.size()) {
                    map.printAnsiMap();
                    allMoved = false;
                    readyToDraw = 0;

                    notifyAll();
                }

                while (allMoved) {
                    wait();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        trips.remove(d);
        System.out.println("I reached the client [D]" + d.getItemId());
    }





    public synchronized void drawMap() {
        while (!allDriversMoved()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        allMoved = true;
        notifyAll();
    }

    private boolean allDriversMoved() {
        assert (trips != null);

        for (TaxiUser d : trips.keySet()) {
            if (d.getPrevPos() == d.getPos()) {
                return false;
            }
        }

        return true;
    }
}