public class Driver extends TaxiUser implements Runnable {

    public Driver(SharedWorld world) {
       super(world);
    }


    @Override
    public void run() {
        world.addToMap(this);
        TaxiUser c = world.waitClient(this);
        world.setItinerary(this, c);
        while(!world.isSamePos(getPos(), c.getPos())){
            world.move(this);
        }
        world.pickUp(c);
        world.setItinerary(this, c);
        while(!world.isSamePos(getPos(), c.getItinerary().get(0))){
            world.move(this);
        }

        System.out.println("I have finished my job! [D]" + Thread.currentThread().getId());
    }


}
