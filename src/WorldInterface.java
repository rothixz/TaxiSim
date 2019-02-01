import java.util.Hashtable;
import java.util.List;

/*
    This is the World Interface
 */
public interface WorldInterface {
    List<Coordinate> getEmptyPos();

    /* Load the map and converts it to a 2D Coordinate map and
     * calculates the empty positions of the map */
    void loadMap();

    /* Method used to visually update the Swing window and to log it to stdout */
    void printAnsiMap();

    /* Adds a TaxiUser to the map, replacing and empty position with himself.
     *  If the TaxiUser is a driver it will be added to the list of drivers
     *  If the TaxiUser is a client it will be added to the list of clients and
     *  assign a destination
     */
    void addToMap(TaxiUser o);

    /* Gets the closest Driver to the Client and adds it to the trips */
    TaxiUser callDriver(TaxiUser c);

    void waitDriver(TaxiUser c, TaxiUser d);

    void waitDelivery(Client c);

    /*  Returns the Client from the trip the driver is associated */
    TaxiUser waitClient(TaxiUser d);

    /* Moves the Driver and the associated trip Client positions to the next position in the Driver's itinerary. */
    void move(TaxiUser o);

    /* Auxiliary function to move a TaxiUser to a position.
     *  It deals with stacked entities positions and updates the origin position to empty
     */
    void moveInMap(TaxiUser o, Coordinate to);

    /* Removes the client from the position and updates the Driver to delivering the client */
    void pickUp(TaxiUser d, TaxiUser c);

    /* Removes both the Driver and Client from the world, updates its positions to empty and
     * removes the trip associated to them */
    void deliver(TaxiUser d, TaxiUser c);

    void available(Driver d);

    /* Get the shortest itinerary between the TaxiUser and a position */
    List<Coordinate> getItenerary(TaxiUser d, Coordinate c);

    TaxiUser getClosestDriver(TaxiUser c);

    List<TaxiUser> getDrivers();

    Hashtable<TaxiUser, TaxiUser> getTrips();

    // Checks if both positions are in the same coordinate
    boolean isSamePos(Coordinate d, Coordinate c);
}
