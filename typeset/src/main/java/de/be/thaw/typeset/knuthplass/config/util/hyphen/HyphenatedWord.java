package de.be.thaw.typeset.knuthplass.config.util.hyphen;

import java.util.List;

/**
 * Representation of a hyphenated word.
 */
public class HyphenatedWord {

    /**
     * Parts of the hyphenated word.
     */
    private final List<HyphenatedWordPart> parts;

    public HyphenatedWord(List<HyphenatedWordPart> parts) {
        this.parts = parts;
    }

    /**
     * Get the parts of the hyphenated word.
     *
     * @return parts
     */
    public List<HyphenatedWordPart> getParts() {
        return parts;
    }

}
