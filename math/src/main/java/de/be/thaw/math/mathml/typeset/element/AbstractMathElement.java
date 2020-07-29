package de.be.thaw.math.mathml.typeset.element;

import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * An abstract math element.
 */
public abstract class AbstractMathElement implements MathElement {

    /**
     * Position of the element.
     */
    private Position position;

    /**
     * Size of the row element.
     */
    @Nullable
    private Size size;

    /**
     * Parent of this math element (if any).
     */
    @Nullable
    private MathElement parent;

    /**
     * Children of the math element.
     */
    private List<MathElement> children;

    public AbstractMathElement(Position position) {
        this.position = position;
    }

    @Override
    public Position getPosition() {
        return getPosition(true);
    }

    /**
     * Get the position of the element.
     *
     * @param absolute whether to get the absolute position
     * @return the position
     */
    public Position getPosition(boolean absolute) {
        Optional<MathElement> optionalParent = getParent();
        if (!absolute || optionalParent.isEmpty()) {
            return position;
        }

        Position parentPos = optionalParent.get().getPosition();
        return new Position(
                parentPos.getX() + position.getX(),
                parentPos.getY() + position.getY()
        );
    }

    @Override
    public Size getSize() {
        if (size == null) {
            // Calculate size as the maximum of the children
            size = calculateTotalSize();
        }

        return size;
    }

    /**
     * Calculate the total size from this elements children.
     *
     * @return size
     */
    protected Size calculateTotalSize() {
        Optional<List<MathElement>> optionalChildren = getChildren();
        if (optionalChildren.isEmpty()) {
            return new Size(0, 0);
        }

        List<MathElement> childElements = optionalChildren.get();

        double minY = Double.MAX_VALUE;
        double maxY = Double.MIN_VALUE;
        double minX = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;

        for (MathElement child : childElements) {
            if (child.getPosition().getY() < minY) {
                minY = child.getPosition().getY();
            }
            if (child.getPosition().getX() < minX) {
                minX = child.getPosition().getX();
            }
            if (child.getPosition().getY() + child.getSize().getHeight() > maxY) {
                maxY = child.getPosition().getY() + child.getSize().getHeight();
            }
            if (child.getPosition().getX() + child.getSize().getWidth() > maxX) {
                maxX = child.getPosition().getX() + child.getSize().getWidth();
            }
        }

        return new Size(maxX - minX, maxY - minY);
    }

    @Override
    public void setSize(@Nullable Size size) {
        this.size = size;
    }

    @Override
    public void setPosition(Position position) {
        this.position = position;
    }

    @Override
    public Optional<MathElement> getParent() {
        return Optional.ofNullable(parent);
    }

    @Override
    public void setParent(@Nullable MathElement parent) {
        this.parent = parent;
    }

    @Override
    public Optional<List<MathElement>> getChildren() {
        return Optional.ofNullable(children);
    }

    @Override
    public void addChild(MathElement element) {
        if (children == null) {
            children = new ArrayList<>();
        }

        children.add(element);
        element.setParent(this);
    }

    @Override
    public double getMidYPosition() {
        return getPosition(false).getY() + (getSize().getHeight()) / 2;
    }

    @Override
    public void scale(double factor) {
        Optional<List<MathElement>> optionalChildren = getChildren();
        if (optionalChildren.isPresent()) {
            for (MathElement child : optionalChildren.get()) {
                child.scale(factor);
            }
        }

        setPosition(new Position(
                getPosition(false).getX() * factor,
                getPosition(false).getY() * factor
        ));
        setSize(new Size(
                getSize().getWidth() * factor,
                getSize().getHeight() * factor
        ));
    }

    @Override
    public double getBaseline() {
        return ((AbstractMathElement) getChildren().orElseThrow().get(0)).getPosition(false).getY() + getChildren().orElseThrow().get(0).getBaseline();
    }

}
