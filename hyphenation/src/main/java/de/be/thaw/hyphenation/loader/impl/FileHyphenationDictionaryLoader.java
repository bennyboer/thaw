package de.be.thaw.hyphenation.loader.impl;

import de.be.thaw.hyphenation.HyphenationDictionary;
import de.be.thaw.hyphenation.impl.DefaultHyphenationDictionary;
import de.be.thaw.hyphenation.loader.HyphenationDictionaryLoader;
import de.be.thaw.hyphenation.loader.exception.HyphenationDictionaryLoadException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Hyphenation dictionary loader loading the dictionary from a file.
 */
public class FileHyphenationDictionaryLoader implements HyphenationDictionaryLoader {

    /**
     * Pattern to match digits in a string.
     */
    private static final Pattern DIGITS_PATTERN = Pattern.compile("[0-9]");

    /**
     * Pattern used to split points.
     */
    private static final Pattern POINTS_SPLIT_PATTERN = Pattern.compile("[.a-z]");

    /**
     * States of the loaders parser.
     */
    private enum State {
        META_DATA,
        DATA
    }

    @Override
    public HyphenationDictionary load(Reader reader) throws HyphenationDictionaryLoadException {
        BufferedReader br;
        if (reader instanceof BufferedReader) {
            br = (BufferedReader) reader;
        } else {
            br = new BufferedReader(reader);
        }

        int leftHyphenMin = 2;
        int rightHyphenMin = 2;

        Map<String, int[]> source = new HashMap<>();

        try {
            State state = State.META_DATA;

            String line;
            while ((line = br.readLine()) != null) {
                line = line.trim();

                boolean isComment = line.startsWith("%");
                if (isComment) {
                    continue;
                }

                switch (state) {
                    case META_DATA -> {
                        String[] parts = line.split(" ");
                        if (parts.length == 1) {
                            if (!parts[0].equalsIgnoreCase("utf-8")) {
                                state = State.DATA;

                                insertPattern(source, line);
                            }
                        } else if (parts.length == 2) {
                            switch (parts[0]) {
                                case "LEFTHYPHENMIN" -> leftHyphenMin = Integer.parseInt(parts[1]);
                                case "RIGHTHYPHENMIN" -> rightHyphenMin = Integer.parseInt(parts[1]);
                            }
                        }
                    }
                    case DATA -> insertPattern(source, line);
                }
            }
        } catch (IOException e) {
            throw new HyphenationDictionaryLoadException("Could not load hyphenation dictionary from file", e);
        }

        return new DefaultHyphenationDictionary(source, leftHyphenMin, rightHyphenMin);
    }

    /**
     * Converting the passed pattern string into a string of characters and
     * a list of points.
     * <p>
     * e. g. 'a1bc3d4' into 'abcd' and [0, 1, 0, 3, 4]
     *
     * @param source  to add pattern to
     * @param pattern to add
     */
    private void insertPattern(Map<String, int[]> source, String pattern) {
        String key = DIGITS_PATTERN.matcher(pattern).replaceAll("");
        int[] points = patternToPoints(pattern);

        source.put(key, points);
    }

    /**
     * Converting a pattern to a points array.
     * <p>
     * e. g. 'a1bc3d4' to [0, 1, 0, 3, 4]
     *
     * @param pattern to convert
     * @return the points array
     */
    private int[] patternToPoints(String pattern) {
        return POINTS_SPLIT_PATTERN.splitAsStream(pattern).mapToInt(s -> {
            if (s.length() == 0) {
                return 0;
            } else {
                return Character.getNumericValue(s.charAt(0));
            }
        }).toArray();
    }

}
