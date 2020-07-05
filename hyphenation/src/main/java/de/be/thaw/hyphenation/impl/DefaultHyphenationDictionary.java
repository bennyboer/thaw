package de.be.thaw.hyphenation.impl;

import de.be.thaw.hyphenation.HyphenationDictionary;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * The default hyphenation dictionary.
 */
public class DefaultHyphenationDictionary implements HyphenationDictionary {

    /**
     * Patterns used to hyphenate.
     */
    private final Map<String, String> patterns;

    /**
     * Minimum characters to hyphenate on the left.
     */
    private final int leftHyphenMin;

    /**
     * Minimum characters to hyphenate on the right.
     */
    private final int rightHyphenMin;

    public DefaultHyphenationDictionary(Map<String, String> patterns, int leftHyphenMin, int rightHyphenMin) {
        this.patterns = patterns;
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
        // TODO

        // Make a sequence of possible cut points.
        char[] cutPoints = new char[word.length() + 1];
        Arrays.fill(cutPoints, '0');

        // Add fake periods to represent begin and end.
        word = "." + word + ".";

        // Find all sub-sequences.
        for (int seqLength = 1; seqLength <= word.length(); seqLength++) {
            for (int start = 0; start <= word.length() - seqLength; start++) {
                String seq = word.substring(start, start + seqLength);
                String value = patterns.get(seq.toLowerCase());
                if (value != null) {
                    // At the beginning of the word we don't count the period.
                    int offset = seq.startsWith(".") ? 0 : -1;

                    // Find the max of the new hints and the existing ones.
                    for (int i = 0; i < value.length(); i++) {
                        char c = value.charAt(i);
                        if (c > cutPoints[start + i + offset]) {
                            cutPoints[start + i + offset] = c;
                        }
                    }
                }
            }
        }

        // Prevent hyphenation at start and end of word.
        for (int i = 0; i < getLeftHyphenMin() && i < cutPoints.length; i++) {
            cutPoints[i] = 0;
        }
        for (int i = 0; i < getRightHyphenMin() && i < cutPoints.length; i++) {
            cutPoints[cutPoints.length - 1 - i] = 0;
        }

        // Remove fake periods.
        word = word.substring(1, word.length() - 1);

        // Find odd numbers and splice there.
        List<String> segments = new ArrayList<>();
        int lastStart = 0;
        for (int i = 0; i < cutPoints.length; i++) {
            if (cutPoints[i] % 2 != 0) {
                segments.add(word.substring(lastStart, i));
                lastStart = i;
            }
        }
        if (lastStart < word.length()) {
            segments.add(word.substring(lastStart));
        }

        // Fix up single hyphens.
        segments = mergeSingleHyphens(segments);

        // Fix up hyphens on the wrong segment.
        segments = moveHyphenPrefixes(segments);

        return segments;
    }

    /**
     * We once saw a problem with the word "super-confort", where the hyphen ended up
     * as its own segment. Merge it with the previous segment.
     */
    private static List<String> mergeSingleHyphens(List<String> segments) {
        // Quick exit.
        if (!segments.contains("-")) {
            return segments;
        }

        // Build up a new list.
        List<String> newSegments = new ArrayList<>(segments.size());

        for (int i = 0; i < segments.size(); i++) {
            String element = segments.get(i);

            if (i + 1 < segments.size() && segments.get(i + 1).equals("-")) {
                newSegments.add(element + "-");
                // Skip the hyphen.
                i++;
            } else {
                newSegments.add(element);
            }
        }

        return newSegments;
    }

    /**
     * We once saw a problem with the word "back-end", which was hyphenated as
     * "back" and "-end". Move the hyphen to the end of the previous segment.
     */
    private static List<String> moveHyphenPrefixes(List<String> segments) {
        // Build up a new list.
        List<String> newSegments = new ArrayList<>(segments.size());

        for (int i = 0; i < segments.size(); i++) {
            String element = segments.get(i);

            if (i + 1 < segments.size() && segments.get(i + 1).startsWith("-")) {
                newSegments.add(element + "-");
                newSegments.add(segments.get(i + 1).substring(1));
                i++;
            } else {
                newSegments.add(element);
            }
        }

        return newSegments;
    }

}
