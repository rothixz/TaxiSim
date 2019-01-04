public class Coordinate {
    private int x, y;
    Coordinate parent;

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
}
