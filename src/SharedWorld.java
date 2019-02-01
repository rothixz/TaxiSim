import java.util.concurrent.Semaphore;

/*
    THis is the shared monitor of the World
 */
public class SharedWorld {
    private final WorldInterface map;
    private Semaphore cli = new Semaphore(1);

    public SharedWorld(WorldInterface map) {
        this.map = map;
    }

    /*
        Adds a TaxiUser to the world, waiting if there are no available empty positions
     */
    public synchronized void addToMap(TaxiUser o) {
        /*
        try {
            if (o instanceof Client) {
                cli.acquire();
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        while (map.getEmptyPos().isEmpty()) {

            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Exception: Interrupted!");
                System.exit(1);
            }
        }

        map.addToMap(o);
        map.printAnsiMap();

        notifyAll();
    }

    /*
       Waits until there are available drivers in the World
    */
    public synchronized TaxiUser callDriver(TaxiUser c) {
        while (map.getDrivers().isEmpty()) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Exception: Interrupted!");
                System.exit(1);
            }
        }

        TaxiUser d = map.callDriver(c);
        notifyAll();

        return d;
    }

    /*
       Waits until the Driver is Client are in the same position
    */
    public synchronized void waitDriver(TaxiUser c, TaxiUser d) {
        while (!map.isSamePos(c.getPos(), d.getPos())) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Exception: Interrupted!");
                System.exit(1);
            }
        }

        map.waitDriver(c, d);
    }

    /*
      Waits until the Driver is Client are in the same position
   */
    public synchronized void waitDelivery(Client c) {
        while (!map.isSamePos(c.getPos(), c.getItinerary().get(0))) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Exception: Interrupted!");
                System.exit(1);
            }
        }

        map.waitDelivery(c);
    }

    /*
       Waits while the Driver has no associated trip
    */
    public synchronized TaxiUser waitClient(TaxiUser d) {
        while (!map.getTrips().containsKey(d)) {
            try {
                wait();
            } catch (InterruptedException e) {
                System.out.println("Exception: Interrupted!");
                System.exit(1);
            }
        }

        return map.waitClient(d);
    }

    /*
       Sets the itinerary of the driver to a position
    */
    public void setItinerary(TaxiUser d, Coordinate c) {
        assert (d != null && c != null);

        d.setItinerary(map.getItenerary(d, c));
    }

    /*
      Moves the TaxiUser from his position to the next position in its itinerary
    */
    public synchronized void move(TaxiUser d) {
        map.move(d);
        map.printAnsiMap();
        try {
            Thread.sleep(700);
        } catch (InterruptedException e) {
            System.out.println("Exception: Interrupted!");
            System.exit(1);
        }
        notifyAll();
    }

    /*
        Picks up a client
    */
    public synchronized void pickUp(TaxiUser d, TaxiUser c) {
        map.pickUp(d, c);
    }

    /*
        Delivers a client
    */
    public synchronized void deliver(TaxiUser d, TaxiUser c) {
        map.deliver(d, c);
        //cli.release();
        notifyAll();
    }

    public synchronized void available(Driver d) {
        map.available(d);
        notifyAll();
    }

    public boolean isSamePos(Coordinate d, Coordinate c) {
        return map.isSamePos(d, c);
    }

}