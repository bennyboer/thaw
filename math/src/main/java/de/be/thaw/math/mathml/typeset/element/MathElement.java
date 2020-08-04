package de.be.thaw.math.mathml.typeset.element;

import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

/**
 * A typeset math element.
 */
public interface MathElement {

    /**
     * Get the size of the element.
     *
     * @return size
     */
    Size getSize();

    /**
     * Set the size of the element.
     *
     * @param size to set
     */
    void setSize(Size size);

    /**
     * Get the position of the element.
     *
     * @return position
     */
    Position getPosition();

    /**
     * Get the position of the element.
     *
     * @param absolute whether to get the absolute position
     * @return the position
     */
    Position getPosition(boolean absolute);

    /**
     * Set the position of the element.
     *
     * @param position to set
     */
    void setPosition(Position position);

    /**
     * Get the math element type.
     *
     * @return type
     */
    MathElementType getType();

    /**
     * Get the children of the element.
     *
     * @return children
     */
    Optional<List<MathElement>> getChildren();

    /**
     * Add the passed element as a child.
     *
     * @param element to add as child element
     */
    void addChild(MathElement element);

    /**
     * Get the parent of the math element.
     *
     * @return parent
     */
    Optional<MathElement> getParent();

    /**
     * Set the parent of the math element.
     *
     * @param parent to set
     */
    void setParent(@Nullable MathElement parent);

    /**
     * Scale the element.
     *
     * @param factor to scale the element with
     */
    void scale(double factor);

    /**
     * Get the baseline of the element.
     *
     * @return baseline
     */
    double getBaseline();

}
