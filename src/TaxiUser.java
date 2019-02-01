import java.util.List;

/*
    This class represents a TaxiUser which can be spawned in the world
 */
public class TaxiUser {
    protected static int unique_id = 1;
    protected final SharedWorld world;
    protected Coordinate pos, prevPos;
    // Unique identifier of the the TaxiUser
    private int id;
    private List<Coordinate> itinerary;

    public TaxiUser(SharedWorld world) {
        this.world = world;
        id = unique_id;
        unique_id++;
    }

    public Coordinate getPos() {
        return pos;
    }

    public void setPos(Coordinate pos) {
        this.pos = pos;
    }

    public void setPrevPos(Coordinate prevPos) {
        this.prevPos = prevPos;
    }

    public List<Coordinate> getItinerary() {
        return itinerary;
    }

    public void setItinerary(List<Coordinate> itinerary) {
        this.itinerary = itinerary;
    }

}
