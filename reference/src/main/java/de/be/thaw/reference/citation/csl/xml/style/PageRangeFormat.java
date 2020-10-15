package de.be.thaw.reference.citation.csl.xml.style;

import com.fasterxml.jackson.annotation.JsonValue;

/**
 * Supported formats for page ranges.
 */
public enum PageRangeFormat {

    /**
     * Unformatted page range.
     * This is the default value when there is no page range format specified.
     */
    UNFORMATTED("", (start, end, delimiter) -> String.format("%d%s%d", start, delimiter, end)),

    /**
     * Abbreviated page ranges are expanded to their non-abbreviated form:
     * 42–45, 321–328, 2787–2816
     */
    EXPANDED("expanded", (start, end, delimiter) -> {
        boolean isEndAbbreviated = end < start;
        if (!isEndAbbreviated) {
            return UNFORMATTED.getFormatter().format(start, end, delimiter);
        }

        String startStr = String.valueOf(start);
        String endStr = String.valueOf(end);
        endStr = String.format(
                "%s%s",
                startStr.substring(0, startStr.length() - endStr.length()),
                endStr
        );

        return String.format("%s%s%s", startStr, delimiter, endStr);
    }),

    /**
     * All digits repeated in the second number are left out:
     * 42–5, 321–8, 2787–816
     */
    MINIMAL("minimal", (start, end, delimiter) -> {
        String startStr = String.valueOf(start);
        String endStr = String.valueOf(end);

        int lenDiff = startStr.length() - endStr.length();
        if (lenDiff >= 0) {
            if (lenDiff > 0) {
                endStr = String.format("%s%s", startStr.substring(0, lenDiff), endStr);
            }

            for (int i = 0; i < startStr.length(); i++) {
                if (startStr.charAt(i) != endStr.charAt(i)) {
                    endStr = endStr.substring(i);
                    break;
                }
            }

            return String.format("%s%s%s", startStr, delimiter, endStr);
        } else {
            return UNFORMATTED.getFormatter().format(start, end, delimiter);
        }
    }),

    /**
     * As "minimal", but at least two digits are kept in the
     * second number when it has two or more digits long.
     */
    MINIMAL_TWO("minimal-two", (start, end, delimiter) -> {
        String startStr = String.valueOf(start);
        String endStr = String.valueOf(end);

        int lenDiff = startStr.length() - endStr.length();
        if (lenDiff >= 0) {
            if (lenDiff > 0) {
                endStr = String.format("%s%s", startStr.substring(0, lenDiff), endStr);
            }

            for (int i = 0; i < startStr.length(); i++) {
                if (startStr.charAt(i) != endStr.charAt(i)) {
                    endStr = endStr.substring(Math.min(i, endStr.length() - 2));
                    break;
                }
            }

            return String.format("%s%s%s", startStr, delimiter, endStr);
        } else {
            return UNFORMATTED.getFormatter().format(start, end, delimiter);
        }
    }),

    /**
     * Page ranges area abbreviated according to the Chicago manual of style rules.
     * <p>
     * | First number | Second number | Examples |
     * | ------------ | ------------- | -------- |
     * | Less than 100 | Use all digits | 3-10; 71-72 |
     * | 100 or multiple of 100 | Use all digits | 100–104; 600–613; 1100–1123 |
     * | 101 through 109 (in multiples of 100) | Use changed part only, omitting unneeded zeros | 107–8; 505–17; 1002–6 |
     * | 110 through 199 (in multiples of 100) | Use two digits, or more as needed | 321–25; 415–532; 11564–68; 13792–803 |
     * | 4 digits | If numbers are four digits long and three digits change, use all digits | 1496–1504; 2787–2816 |
     */
    CHICAGO("chicago", (start, end, delimiter) -> {
        if (start < 100) {
            return UNFORMATTED.getFormatter().format(start, end, delimiter);
        } else if (start % 100 == 0) {
            return UNFORMATTED.getFormatter().format(start, end, delimiter);
        } else if (start % 100 <= 9) {
            return MINIMAL.getFormatter().format(start, end, delimiter);
        } else if (String.valueOf(start).length() >= 4) {
            // Check if length - 1 digits have changed
            int changeCount = 0;
            String startStr = String.valueOf(start);
            String endStr = String.valueOf(end);
            if (endStr.length() == startStr.length()) {
                for (int i = 1; i < endStr.length(); i++) {
                    if (startStr.charAt(i) != endStr.charAt(i)) {
                        changeCount++;
                    }
                }

                if (changeCount == endStr.length() - 1) {
                    return String.format("%s%s%s", startStr, delimiter, endStr);
                }
            }
        }

        return MINIMAL_TWO.getFormatter().format(start, end, delimiter);
    });

    /**
     * Value used in the CSL specification.
     */
    private final String value;

    /**
     * Formatter for page ranges.
     */
    private final PageRangeFormatter formatter;

    PageRangeFormat(String value, PageRangeFormatter formatter) {
        this.value = value;
        this.formatter = formatter;
    }

    /**
     * Get the value used in the CSL specification.
     *
     * @return value
     */
    @JsonValue
    public String getValue() {
        return value;
    }

    /**
     * Get the formatter to use to format page ranges.
     *
     * @return formatter
     */
    public PageRangeFormatter getFormatter() {
        return formatter;
    }

    /**
     * Formatter for page ranges.
     */
    public interface PageRangeFormatter {

        /**
         * Format the passed page range.
         *
         * @param start     of the page range
         * @param end       of the page range
         * @param delimiter of the page range
         * @return the formatted page range
         */
        String format(int start, int end, String delimiter);

    }

}
