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

    public TaxiUser callDriver(TaxiUser c) {
        synchronized (this) {
            while (map.getEmptyPos().isEmpty()) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            try {
                cli.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            map.callDriver(c);
            notifyAll();

            while (availableDrivers.isEmpty()) {
                try {
                    //System.out.println("CallDriver wait");
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            //System.out.println("CallDriver gonna call");
            TaxiUser d = getClosestDriver(c);
            availableDrivers.remove(d);
            trips.put(d, c);
            notifyAll();
            return d;
        }
    }

    public synchronized TaxiUser waitClient(TaxiUser d) {
        assert (!map.getEmptyPos().isEmpty());

        map.addTaxiUser(d);
        assert (d.getPos() != null);

        availableDrivers.add(d);
        assert (!availableDrivers.isEmpty());

        notifyAll();

        while (!trips.containsKey(d)) {
            try {
                //System.out.println("Waiting for a client to choose me [D]" + Thread.currentThread().getId());
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        //System.out.println("A client chose me [D]" + Thread.currentThread().getId());

        assert (!trips.isEmpty());

        return trips.get(d);
    }

    public void waitDriver(TaxiUser c, TaxiUser d) {
        assert (trips.containsValue(c));

        synchronized (this) {
            while (isSamePos(c, d)) {
                try {
                    //System.out.println("Waiting for the driver to pick me up [C]" + Thread.currentThread().getId());
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }

        //System.out.println("Driver picked me up [C]" + Thread.currentThread().getId());
        cli.release();
    }

    public synchronized void driveToClient(TaxiUser d, TaxiUser c) {
        assert (d.getPos() != null && c.getPos() != null);

        List<Coordinate> itinerary = MapUtils.getShortestItenerary(map, c.getPos(), d.getPos());

        assert (itinerary.size() > 1);

        try {
            while (!isSamePos(d, c)) {
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

    private TaxiUser getClosestDriver(TaxiUser c) {
        assert (c.getPos() != null);

        double minDuration = Integer.MAX_VALUE;
        TaxiUser d = null;

        for (TaxiUser obj : availableDrivers) {
            double eta = MapUtils.getIteneraryDuration(MapUtils.getShortestItenerary(map, c.getPos(), obj.getPos()));
            if (eta < minDuration) {
                minDuration = eta;
                d = obj;
            }
        }

        assert (d != null);

        return d;
    }

    private boolean isSamePos(TaxiUser d, TaxiUser c) {
        //System.out.println("Driver in pos: (" + d.getPos().getX() + ", " + d.getPos().getY() + ") and client in pos (" + c.getPos().getX() + ", " + c.getPos().getY() +")");
        return d.getPos().getX() == c.getPos().getX() && d.getPos().getY() == c.getPos().getY();
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