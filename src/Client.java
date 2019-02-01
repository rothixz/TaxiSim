/*
    This class represents the Client entity which wants to go to a destination
 */
public class Client extends TaxiUser implements Runnable {
    public Client(SharedWorld world) {
        super(world);
    }

    /*
        The Client's lifecycle is to join the world, call a driver, wait for
        the driver to pick him up and
    */
    @Override
    public void run() {
            world.addToMap(this);
            TaxiUser d = world.callDriver(this);
            world.waitDriver(this, d);
            world.waitDelivery(this);
    }
}
