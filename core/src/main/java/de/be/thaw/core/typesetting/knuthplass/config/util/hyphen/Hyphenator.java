package de.be.thaw.core.typesetting.knuthplass.config.util.hyphen;

/**
 * Interface used to hyphenate words.
 */
public interface Hyphenator {

    /**
     * Hyphenate the passed word.
     *
     * @param word to hyphenate
     * @return the split word after hyphenating it
     */
    HyphenatedWord hyphenate(String word);

    /**
     * Get the penalty for an explicit hyphen.
     *
     * @return penalty
     */
    double getExplicitHyphenPenalty();

}
