package de.be.thaw.typeset.page;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.util.Position;
import de.be.thaw.typeset.util.Size;

import java.util.Optional;

/**
 * An abstract element.
 */
public abstract class AbstractElement implements Element {

    /**
     * Number of the page the element is on.
     */
    private final int pageNumber;

    /**
     * Size of the element.
     */
    private final Size size;

    /**
     * Position of the element.
     */
    private final Position position;

    public AbstractElement(int pageNumber, Size size, Position position) {
        this.pageNumber = pageNumber;
        this.size = size;
        this.position = position;
    }

    @Override
    public int getPageNumber() {
        return pageNumber;
    }

    @Override
    public Size getSize() {
        return size;
    }

    @Override
    public Position getPosition() {
        return position;
    }

    @Override
    public Optional<DocumentNode> getNode() {
        return Optional.empty();
    }

}
