public class Driver extends Thread {
    private final World world;
    private Coordinate pos, prevPos;

    public Driver(Coordinate pos, World world) {
       this.pos = pos;
       this.prevPos = pos;
       this.world = world;
    }

    public Coordinate getPos() {
        return pos;
    }

    public void setPos(Coordinate pos) {
        this.pos = pos;
    }

    public Coordinate getPrevPos() {
        return prevPos;
    }

    public void setPrevPos(Coordinate prevPos) {
        this.prevPos = prevPos;
    }

    @Override
    public void run() {
        Client c  = world.waitClient(this);
        world.driveToClient(this, c);
        System.out.println("I have finished my job! [D]" + Thread.currentThread().getId());
    }
}
