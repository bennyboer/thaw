package de.be.thaw.style.parser.lexer.token;

/**
 * An enumeration of available token types in the style format.
 */
public enum StyleFormatTokenType {

    /**
     * Name in a block start.
     * For example "h1" and "h2" in "h1, h2.test { ... }".
     */
    BLOCK_START_NAME,

    /**
     * The comma between multiple items in the block start.
     * For example the commas in "h1, h2, h3 { ... }".
     */
    BLOCK_START_SEPARATOR,

    /**
     * Separator for separating names and classes in the block start.
     * For example the dot (".") character in "h1, h2.test { ... }"
     * separating "h2" and the class name "test".
     */
    BLOCK_START_CLASS_SEPARATOR,

    /**
     * The name of a class (starting with a dot (".")).
     * For example "test" in "h1.test { ... }".
     */
    BLOCK_START_CLASS_NAME,

    /**
     * Separator that separated a pseudo class from the other names in a block start.
     * For example ":" in "page:last-page { ... }".
     */
    BLOCK_START_PSEUDO_CLASS_SEPARATOR,

    /**
     * Name of a pseudo class.
     * For example "last-page" in "page:last-page { ... }".
     */
    BLOCK_START_PSEUDO_CLASS_NAME,

    /**
     * Start of settings for a pseudo class.
     * For example "(" in "page:page(2) { ... }".
     */
    BLOCK_START_PSEUDO_CLASS_SETTINGS_START,

    /**
     * End of settings for a pseudo class.
     * For example ")" in "page:page(2) { ... }".
     */
    BLOCK_START_PSEUDO_CLASS_SETTINGS_END,

    /**
     * A setting value of a pseudo class.
     * For example "2" in "page:page(2) { ... }".
     */
    BLOCK_START_PSEUDO_CLASS_SETTING,

    /**
     * Separator that separated settings in a pseudo class.
     * For example "," in "page:page(1, 2) { ... }".
     */
    BLOCK_START_PSEUDO_CLASS_SETTING_SEPARATOR,

    /**
     * A token that signals a block opening.
     * For example "{" in "page { ... }".
     */
    BLOCK_OPEN,

    /**
     * A token that signals a block ending.
     * For example "}" in "page { ... }".
     */
    BLOCK_CLOSE,

    /**
     * A property (key of an entry) in a block.
     * For example "font-family" in "document {
     * font-family: "Arial";
     * }"
     */
    PROPERTY,

    /**
     * A separator separating property and value of an entry in a block.
     * For example ":" in "document {
     * font-family: "Arial";
     * }"
     */
    PROPERTY_VALUE_SEPARATOR,

    /**
     * A value of an entry in a style block.
     * For example "\"Arial\"" in "document {
     * font-family: "Arial";
     * }"
     */
    VALUE,

    /**
     * The end of a entry in a style block.
     * For example ";" in "document {
     * font-family: "Arial";
     * }"
     */
    VALUE_END,

    /**
     * A single line comment starting with "//".
     */
    SINGLE_LINE_COMMENT,

    /**
     * A multi line comment starting with "/*" and ending with "*\/".
     */
    MULTI_LINE_COMMENT,

    /**
     * Token that does not carry useful information.
     */
    IGNORE

}
