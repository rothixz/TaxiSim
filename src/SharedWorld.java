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

    public synchronized void addToMap(TaxiUser o) {
        while (map.getEmptyPos().isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        try {
            if (o instanceof Client) {
                cli.acquire();
            } else if (o instanceof Driver) {
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

    public synchronized void setItinerary(TaxiUser d, TaxiUser c) {
        assert (d.getPos() != null && c.getPos() != null);

        ((Driver) d).setItinerary(map.getItenerary(d, c));
    }

    public synchronized void move(TaxiUser d){
        map.move(d);
        map.printAnsiMap();
        notifyAll();
    }

    public boolean isSamePos(TaxiUser d, TaxiUser c) {
       return map.isSamePos(d, c);
    }

    //trips.remove(d);
}