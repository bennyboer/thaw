package de.be.thaw.cli;

/**
 * Enumeration of possible error results of the CLI.
 */
public enum ErrorResult {

    OK(0),
    MISSING_TEXT_FILE(1),
    MORE_THAN_ONE_TEXT_FILE(2),
    TEXT_FILE_PARSING_ERROR(3),
    EXPORT_ERROR(4);

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
