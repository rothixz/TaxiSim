/*
    This class represents the Driver entity which picks and delivers clients
*/
public class Driver extends TaxiUser implements Runnable {
    // Represents if the driver is delivering a client or not
    private boolean delivering;

    public Driver(SharedWorld world) {
        super(world);
        delivering = false;
    }

    public boolean isDelivering() {
        return delivering;
    }

    public void setDelivering(boolean delivering) {
        this.delivering = delivering;
    }

    /*
       The Drivers's lifecycle is to join the world, wait for a client to choose him,
       pick him up and deliver him to his destination.
    */
    @Override
    public void run() {
        world.addToMap(this);
        while (true) {
            TaxiUser c = world.waitClient(this);
            world.setItinerary(this, c.getPos());
            while (!world.isSamePos(getPos(), c.getPos())) {
                world.move(this);
            }
            world.pickUp(this, c);
            world.setItinerary(this, c.getItinerary().get(0));
            while (!world.isSamePos(getPos(), c.getItinerary().get(0))) {
                world.move(this);
            }
            world.deliver(this, c);
            world.available(this);
        }
    }
}
