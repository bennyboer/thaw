package de.be.thaw.util;

/**
 * Object representing a position on a 2-dimensional plane.
 */
public class Position {

    /**
     * The x-offset from the zero point.
     */
    private final double x;

    /**
     * The y-offset from the zero point.
     */
    private final double y;

    public Position(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Get the x-offset.
     *
     * @return x-offset
     */
    public double getX() {
        return x;
    }

    /**
     * Get the y-offset.
     *
     * @return y-offset
     */
    public double getY() {
        return y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Position position = (Position) o;

        if (Double.compare(position.x, x) != 0) return false;
        return Double.compare(position.y, y) == 0;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("(%f, %f)", getX(), getY());
    }

}
