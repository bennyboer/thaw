package de.be.thaw.util;

/**
 * Bounds of something in form of a rectangle.
 */
public class Bounds {

    /**
     * The position of something.
     */
    private final Position position;

    /**
     * The size of something.
     */
    private final Size size;

    public Bounds(Position position, Size size) {
        this.position = position;
        this.size = size;
    }

    /**
     * Get the position.
     *
     * @return position
     */
    public Position getPosition() {
        return position;
    }

    /**
     * Get the size.
     *
     * @return size
     */
    public Size getSize() {
        return size;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bounds bounds = (Bounds) o;

        if (position != null ? !position.equals(bounds.position) : bounds.position != null) return false;
        return size != null ? size.equals(bounds.size) : bounds.size == null;
    }

    @Override
    public int hashCode() {
        int result = position != null ? position.hashCode() : 0;
        result = 31 * result + (size != null ? size.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return String.format("%s at %s",
                getSize().toString(),
                getPosition().toString()
        );
    }

}
