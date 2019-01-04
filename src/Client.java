public class Client  extends Thread{
    private final World world;
    private Coordinate pos, prevPos;

    public Client(Coordinate pos, World world) {
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
        Driver d = world.callDriver(this);
        world.waitDriver(this, d);
    }
}
