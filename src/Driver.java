public class Driver extends TaxiUser implements Runnable {

    public Driver(SharedWorld world) {
       super(world);
    }


    @Override
    public void run() {
        world.addToMap(this);
        TaxiUser c = world.waitClient(this);
        world.setItinerary(this, c);
        while(!world.isSamePos(this, c)){
            world.move(this);
        }

        System.out.println("I have finished my job! [D]" + Thread.currentThread().getId());
    }


}
