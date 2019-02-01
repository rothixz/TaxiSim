/*
    This class represents a 2D Coordinate of a map
 */
public class Coordinate {
    // Auxiliary variable representing the previous coordinate in an itinerary
    Coordinate parent;
    private int x, y;

    public Coordinate(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public Coordinate(int x, int y, Coordinate parent) {
        this.x = x;
        this.y = y;
        this.parent = parent;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public Coordinate getParent() {
        return parent;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }

        if (!Coordinate.class.isAssignableFrom(obj.getClass())) {
            return false;
        }

        final Coordinate other = (Coordinate) obj;

        if (this.x != other.x || this.y != other.y) {
            return false;
        }

        return true;
    }

    @Override
    public String toString() {
        return "X: " + x + " Y: " + y;
    }
}
