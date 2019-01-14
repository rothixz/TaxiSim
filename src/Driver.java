public class Driver extends TaxiUser implements Runnable {
    public Driver(World world) {
       super(world);
    }

    @Override
    public void run() {
        TaxiUser c  = world.waitClient(this);
        world.driveToClient(this, c);
        System.out.println("I have finished my job! [D]" + Thread.currentThread().getId());
    }
}
