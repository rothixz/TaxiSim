import java.util.List;

public class TaxiUser {
    protected final SharedWorld world;
    protected Coordinate pos, prevPos;
    protected static int unique_id = 1;
    private int itemId;
    private List<Coordinate> itinerary;

    public TaxiUser(SharedWorld world) {
        this.world = world;
        itemId = unique_id;
        unique_id++;
    }

    public Coordinate getPos() {
        return pos;
    }


    public int getItemId() {
        return itemId;
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

    public void setItinerary(List<Coordinate> itinerary) {
        this.itinerary = itinerary;
    }


    public List<Coordinate> getItinerary() {
        return itinerary;
    }

}
