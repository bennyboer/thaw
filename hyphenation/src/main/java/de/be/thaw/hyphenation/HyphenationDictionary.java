package de.be.thaw.hyphenation;

import java.util.List;

/**
 * Dictionary for hyphenation.
 */
public interface HyphenationDictionary {

    /**
     * Get the minimum length of a hyphenated word part from left.
     *
     * @return minimum left hyphen length
     */
    int getLeftHyphenMin();

    /**
     * Get the minimum length of a hyphenated word part from right.
     *
     * @return minimum right hyphen length
     */
    int getRightHyphenMin();

    /**
     * Hyphenate the passed word.
     *
     * @param word to hyphenate
     * @return the hyphenated word parts
     */
    List<String> hyphenate(String word);

}
