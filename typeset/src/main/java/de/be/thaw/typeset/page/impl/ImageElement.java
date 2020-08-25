package de.be.thaw.typeset.page.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.typeset.knuthplass.config.util.image.ImageSource;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.ElementType;

import java.util.Optional;

/**
 * An element representing an image.
 */
public class ImageElement extends AbstractElement {

    /**
     * The source of the image.
     */
    private final ImageSource src;

    /**
     * The original node in the thaw document.
     */
    private final DocumentNode node;

    public ImageElement(ImageSource src, DocumentNode node, int pageNumber, Size size, Position position) {
        super(pageNumber, size, position);

        this.src = src;
        this.node = node;
    }

    @Override
    public ElementType getType() {
        return ElementType.IMAGE;
    }

    /**
     * Get the image source.
     *
     * @return image source
     */
    public ImageSource getSrc() {
        return src;
    }

    @Override
    public Optional<DocumentNode> getNode() {
        return Optional.of(node);
    }

}
