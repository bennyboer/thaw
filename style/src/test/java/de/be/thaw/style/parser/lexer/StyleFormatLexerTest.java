package de.be.thaw.style.parser.lexer;

import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatToken;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.List;
import java.util.stream.Collectors;

public class StyleFormatLexerTest {

    /**
     * Tokenize the passed text in Thaw style format.
     *
     * @param text to tokenize
     * @return the tokens
     */
    private List<StyleFormatToken> tokenize(String text) throws StyleFormatLexerException {
        return StyleFormatLexerFactory.getInstance().getLexer().process(new StringReader(text));
    }

    /**
     * Convert the passed tokens to a string.
     *
     * @param tokens to convert
     * @return stringyfied tokens
     */
    private String tokensToString(List<StyleFormatToken> tokens) {
        return tokens.stream()
                .map(StyleFormatToken::toString)
                .collect(Collectors.joining("\n"));
    }

    @Test
    public void simpleTest1() throws StyleFormatLexerException {
        String text = "document {\n" +
                "   font-family: Arial;\n" +
                "}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'document ' (Start: 1:1, End: 1:10)\n" +
                "[BLOCK_OPEN] '{\n" +
                "   ' (Start: 1:10, End: 2:4)\n" +
                "[PROPERTY] 'font-family' (Start: 2:4, End: 2:15)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 2:15, End: 2:17)\n" +
                "[VALUE] 'Arial' (Start: 2:17, End: 2:22)\n" +
                "[VALUE_END] ';\n" +
                "' (Start: 2:22, End: 3:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 3:1, End: 3:2)", tokensToString(tokens));
    }

    @Test
    public void simpleTest1Minimized() throws StyleFormatLexerException {
        String text = "document{font-family:Arial;}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'document' (Start: 1:1, End: 1:9)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:9, End: 1:10)\n" +
                "[PROPERTY] 'font-family' (Start: 1:10, End: 1:21)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ':' (Start: 1:21, End: 1:22)\n" +
                "[VALUE] 'Arial' (Start: 1:22, End: 1:27)\n" +
                "[VALUE_END] ';' (Start: 1:27, End: 1:28)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:28, End: 1:29)", tokensToString(tokens));
    }

    @Test
    public void simpleTest1WithSingleLineComments() throws StyleFormatLexerException {
        String text = "document { // Hello world I am a comment!\n" +
                "   font-family: Arial; // Another comment\n" +
                "} // Hey there!";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'document ' (Start: 1:1, End: 1:10)\n" +
                "[BLOCK_OPEN] '{ ' (Start: 1:10, End: 1:12)\n" +
                "[SINGLE_LINE_COMMENT] '// Hello world I am a comment!' (Start: 1:12, End: 1:42)\n" +
                "[BLOCK_OPEN] '\n" +
                "   ' (Start: 1:42, End: 2:4)\n" +
                "[PROPERTY] 'font-family' (Start: 2:4, End: 2:15)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 2:15, End: 2:17)\n" +
                "[VALUE] 'Arial' (Start: 2:17, End: 2:22)\n" +
                "[VALUE_END] '; ' (Start: 2:22, End: 2:24)\n" +
                "[SINGLE_LINE_COMMENT] '// Another comment' (Start: 2:24, End: 2:42)\n" +
                "[VALUE_END] '\n" +
                "' (Start: 2:42, End: 3:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 3:1, End: 3:2)\n" +
                "[IGNORE] ' ' (Start: 3:2, End: 3:3)\n" +
                "[SINGLE_LINE_COMMENT] '// Hey there!' (Start: 3:3, End: 3:16)", tokensToString(tokens));
    }

    @Test
    public void simpleTest1WithMultiLineComments() throws StyleFormatLexerException {
        String text = "document { /* MULTILINE\n" +
                " YAY\n" +
                "   */\n" +
                "   font-family: Arial; /* Me again! */\n" +
                "}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'document ' (Start: 1:1, End: 1:10)\n" +
                "[BLOCK_OPEN] '{ ' (Start: 1:10, End: 1:12)\n" +
                "[MULTI_LINE_COMMENT] '/* MULTILINE\n" +
                " YAY\n" +
                "   */' (Start: 1:12, End: 3:6)\n" +
                "[BLOCK_OPEN] '\n" +
                "   ' (Start: 3:6, End: 4:4)\n" +
                "[PROPERTY] 'font-family' (Start: 4:4, End: 4:15)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 4:15, End: 4:17)\n" +
                "[VALUE] 'Arial' (Start: 4:17, End: 4:22)\n" +
                "[VALUE_END] '; ' (Start: 4:22, End: 4:24)\n" +
                "[MULTI_LINE_COMMENT] '/* Me again! */' (Start: 4:24, End: 4:39)\n" +
                "[VALUE_END] '\n" +
                "' (Start: 4:39, End: 5:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 5:1, End: 5:2)", tokensToString(tokens));
    }

}
