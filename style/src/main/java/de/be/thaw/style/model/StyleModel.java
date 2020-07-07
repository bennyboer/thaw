package de.be.thaw.style.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.parser.impl.StyleModelDeserializer;

import java.util.Map;
import java.util.Optional;

/**
 * Representation of the Thaw document style format model.
 */
@JsonDeserialize(using = StyleModelDeserializer.class)
public class StyleModel {

    /**
     * Map of blocks in the model.
     * Mapped by their name.
     */
    private final Map<String, StyleBlock> blocks;

    public StyleModel(Map<String, StyleBlock> blocks) {
        this.blocks = blocks;
    }

    /**
     * Get a block by its name.
     *
     * @param name to get block for
     * @return block
     */
    public Optional<StyleBlock> getBlock(String name) {
        return Optional.ofNullable(blocks.get(name.toLowerCase()));
    }

}
