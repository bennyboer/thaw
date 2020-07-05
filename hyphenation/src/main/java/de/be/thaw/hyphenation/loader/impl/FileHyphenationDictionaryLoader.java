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

        Map<String, String> patterns = new HashMap<>();

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

                                addPattern(patterns, line);
                            }
                        } else if (parts.length == 2) {
                            switch (parts[0]) {
                                case "LEFTHYPHENMIN" -> leftHyphenMin = Integer.parseInt(parts[1]);
                                case "RIGHTHYPHENMIN" -> rightHyphenMin = Integer.parseInt(parts[1]);
                            }
                        }
                    }
                    case DATA -> {
                        addPattern(patterns, line);
                    }
                }
            }
        } catch (IOException e) {
            throw new HyphenationDictionaryLoadException("Could not load hyphenation dictionary from file", e);
        }

        return new DefaultHyphenationDictionary(patterns, leftHyphenMin, rightHyphenMin);
    }

    /**
     * Add a pattern to the passed map.
     *
     * @param patterns to add pattern to
     * @param pattern  to add
     */
    private void addPattern(Map<String, String> patterns, String pattern) {
        String key = DIGITS_PATTERN.matcher(pattern).replaceAll("");
        String value = transformValue(pattern);

        patterns.put(key, value);
    }

    private String transformValue(String pattern) {
        StringBuilder builder = new StringBuilder(pattern);

        if (builder.length() > 0) {
            // Remove leading and trailing dots '.' as they only mean the beginning of words
            if (builder.charAt(0) == '.') {
                builder.deleteCharAt(0);
            }

            if (builder.charAt(builder.length() - 1) == '.') {
                builder.deleteCharAt(builder.length() - 1);
            }
        }

        // Insert missing zeros.
        for (int i = 0; i <= builder.length(); i += 2) {
            if (i == builder.length() || !Character.isDigit(builder.charAt(i))) {
                builder.insert(i, '0');
            }
        }

        // Removing all characters
        for (int i = builder.length() - 1; i >= 0; i--) {
            if (!Character.isDigit(builder.charAt(i))) {
                builder.deleteCharAt(i);
            }
        }

        return builder.toString();
    }

}
