package de.be.thaw.style.model;

import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.selector.StyleSelector;
import de.be.thaw.style.model.style.Styles;

import java.util.List;
import java.util.Optional;

/**
 * Representation of the Thaw document style model.
 */
public interface StyleModel {

    /**
     * Get all style blocks in the model.
     *
     * @return style blocks
     */
    List<StyleBlock> getBlocks();

    /**
     * Add the passed style block.
     *
     * @param block to add
     */
    void addBlock(StyleBlock block);

    /**
     * Get the style block with the exact same selector as the passed one.
     *
     * @param selector to get block by
     * @return style block (or an empty Optional)
     */
    Optional<StyleBlock> getBlock(StyleSelector selector);

    /**
     * Select a subset of styles from the model.
     * The passed selectors are taken in descending priority.
     * For example when you select a code and a paragraph block, with the code selector first,
     * then the styles of the code style block will be applied with higher priority than the styles
     * of the paragraph style block.
     *
     * @param selectors to select styles with
     * @return styles that apply on the passed settings in the selector
     */
    Styles select(StyleSelector... selectors);

    /**
     * Merge this style model with another one.
     *
     * @param other model to merge with
     * @return the merged model
     */
    StyleModel merge(StyleModel other);

}
