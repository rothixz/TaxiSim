public class Client  extends TaxiUser implements  Runnable {
    public Client(World world) {
        super(world);
    }

    @Override
    public void run() {
        TaxiUser d = world.callDriver(this);
        world.waitDriver(this, d);
    }
}
