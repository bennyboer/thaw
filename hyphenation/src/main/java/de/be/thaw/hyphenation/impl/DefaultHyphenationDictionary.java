package de.be.thaw.hyphenation.impl;

import de.be.thaw.hyphenation.HyphenationDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * The default hyphenation dictionary.
 */
public class DefaultHyphenationDictionary implements HyphenationDictionary {

    /**
     * Points for the individual hyphenation keys.
     */
    private final Map<String, int[]> source;

    /**
     * Minimum characters to hyphenate on the left.
     */
    private final int leftHyphenMin;

    /**
     * Minimum characters to hyphenate on the right.
     */
    private final int rightHyphenMin;

    public DefaultHyphenationDictionary(Map<String, int[]> source, int leftHyphenMin, int rightHyphenMin) {
        this.source = source;
        this.leftHyphenMin = leftHyphenMin;
        this.rightHyphenMin = rightHyphenMin;
    }

    @Override
    public int getLeftHyphenMin() {
        return leftHyphenMin;
    }

    @Override
    public int getRightHyphenMin() {
        return rightHyphenMin;
    }

    @Override
    public List<String> hyphenate(String word) {
        // Words that are too short are not to be hyphenated!
        if (word.length() <= getLeftHyphenMin() + getRightHyphenMin()) {
            return Collections.singletonList(word);
        }

        // Prepare the word for the hyphenation algorithm
        word = '.' + word + '.';

        int[] points = new int[word.length() + 1];
        Arrays.fill(points, 0);

        int len = word.length();
        for (int partLength = 1; partLength < len; partLength++) {
            for (int start = 0; start <= word.length() - partLength; start++) {
                String part = word.substring(start, start + partLength).toLowerCase();

                int[] curPoints = source.get(part);
                if (curPoints != null) {
                    for (int p = 0; p < curPoints.length; p++) {
                        points[start + p] = Math.max(points[start + p], curPoints[p]);
                    }
                }
            }
        }

        // Preventing hyphenation at the leading (leftHyphenMin) and trailing (rightHyphenMin) part of the word
        for (int i = 0; i < getLeftHyphenMin(); i++) {
            points[i] = 0;
        }
        for (int i = points.length - 1; i >= points.length - getRightHyphenMin(); i--) {
            points[i] = 0;
        }

        // Removing periods again
        word = word.substring(1, word.length() - 1);

        // Build the list of word parts
        List<String> parts = new ArrayList<>();

        int startIndex = 0;
        for (int i = getLeftHyphenMin(); i < word.length() - getRightHyphenMin() + 1; i++) {
            int point = points[i + 1];

            boolean isOdd = point % 2 != 0;
            if (isOdd) {
                parts.add(word.substring(startIndex, i));
                startIndex = i;
            }
        }

        // Add the rest of the word
        if (startIndex < word.length()) {
            parts.add(word.substring(startIndex));
        }

        return parts;
    }

}
