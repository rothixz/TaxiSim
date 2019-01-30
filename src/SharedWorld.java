import java.util.concurrent.Semaphore;

public class SharedWorld {
    private final World map;
    private Semaphore cli = new Semaphore(3);
    private Semaphore dri = new Semaphore(3);

    public SharedWorld(World map) {
        this.map = map;
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
        map.printAnsiMap();

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

        return map.waitClient(d);
    }

    public synchronized void setItinerary(TaxiUser d, TaxiUser c) {
        assert (d.getPos() != null && c.getPos() != null);

        d.setItinerary(map.getItenerary(d, c));
    }

    public synchronized void move(TaxiUser d){
        map.move(d);
        notifyAll();
    }

    public synchronized void pickUp(TaxiUser c) {
        map.pickUp(c);
    }

    public boolean isSamePos(Coordinate d, Coordinate c) {
       return map.isSamePos(d, c);
    }



    //trips.remove(d);
}