package de.be.thaw.cli;

/**
 * Enumeration of possible error results of the CLI.
 */
public enum ErrorResult {

    OK(0),
    MISSING_TEXT_FILE(1),
    MORE_THAN_ONE_TEXT_FILE(2),
    TEXT_FILE_PARSING_ERROR(3),
    EXPORT_ERROR(4),
    MORE_THAN_ONE_INFO_FILE(5),
    INFO_FILE_PARSING_ERROR(6),
    MORE_THAN_ONE_STYLE_FILE(7),
    STYLE_FILE_PARSING_ERROR(8),
    MORE_THAN_ONE_SOURCE_FILE(9),
    SOURCE_FILE_PARSING_ERROR(10),
    ROOT_CACHE_CLEANING_ERROR(11);

    /**
     * Code of the error.
     */
    private final int code;

    ErrorResult(int code) {
        this.code = code;
    }

    /**
     * Get the error code.
     *
     * @return code
     */
    public int getCode() {
        return code;
    }

}
