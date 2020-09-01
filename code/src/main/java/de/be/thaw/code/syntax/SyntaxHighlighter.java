package de.be.thaw.code.syntax;

import de.be.thaw.code.syntax.exception.HighlightException;

/**
 * Something that is able to highlight the syntax of code.
 */
public interface SyntaxHighlighter {

    /**
     * Highlight the passed code in the given language.
     *
     * @param code     to highlight
     * @param language the code is written in
     * @param style    name of the style to format the code with
     * @return the highlighted code
     * @throws HighlightException in case the code could not be highlighted
     */
    String highlight(String code, String language, String style) throws HighlightException;

}
