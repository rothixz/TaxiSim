public class TaxiUser {
    protected final World world;
    protected Coordinate pos, prevPos;

    public TaxiUser(Coordinate pos, World world) {
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
}
