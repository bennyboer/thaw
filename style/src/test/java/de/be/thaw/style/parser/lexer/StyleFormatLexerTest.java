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

    @Test
    public void testEmptyBlock1() throws StyleFormatLexerException {
        String text = "h1 {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1 ' (Start: 1:1, End: 1:4)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:4, End: 1:5)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:5, End: 1:6)", tokensToString(tokens));
    }

    @Test
    public void testEmptyBlock2() throws StyleFormatLexerException {
        String text = "h1 {\n" +
                "\n" +
                "}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1 ' (Start: 1:1, End: 1:4)\n" +
                "[BLOCK_OPEN] '{\n" +
                "\n" +
                "' (Start: 1:4, End: 3:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 3:1, End: 3:2)", tokensToString(tokens));
    }

    @Test
    public void testMultipleBlocks() throws StyleFormatLexerException {
        String text = "h1 {\n" +
                "\n" +
                "}\n" +
                "\n" +
                "h2{}\n" +
                "document {\n" +
                "}\n" +
                "\n";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1 ' (Start: 1:1, End: 1:4)\n" +
                "[BLOCK_OPEN] '{\n" +
                "\n" +
                "' (Start: 1:4, End: 3:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 3:1, End: 3:2)\n" +
                "[IGNORE] '\n" +
                "\n" +
                "' (Start: 3:2, End: 5:1)\n" +
                "[BLOCK_START_NAME] 'h2' (Start: 5:1, End: 5:3)\n" +
                "[BLOCK_OPEN] '{' (Start: 5:3, End: 5:4)\n" +
                "[BLOCK_CLOSE] '}' (Start: 5:4, End: 5:5)\n" +
                "[IGNORE] '\n" +
                "' (Start: 5:5, End: 6:1)\n" +
                "[BLOCK_START_NAME] 'document ' (Start: 6:1, End: 6:10)\n" +
                "[BLOCK_OPEN] '{\n" +
                "' (Start: 6:10, End: 7:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 7:1, End: 7:2)\n" +
                "[IGNORE] '\n" +
                "\n" +
                "' (Start: 7:2, End: 9:1)", tokensToString(tokens));
    }

    @Test
    public void testMultipleStyleNames() throws StyleFormatLexerException {
        String text = "h1, h2, h3, h4, h5, h6 {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:3, End: 1:5)\n" +
                "[BLOCK_START_NAME] 'h2' (Start: 1:5, End: 1:7)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:7, End: 1:9)\n" +
                "[BLOCK_START_NAME] 'h3' (Start: 1:9, End: 1:11)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:11, End: 1:13)\n" +
                "[BLOCK_START_NAME] 'h4' (Start: 1:13, End: 1:15)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:15, End: 1:17)\n" +
                "[BLOCK_START_NAME] 'h5' (Start: 1:17, End: 1:19)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:19, End: 1:21)\n" +
                "[BLOCK_START_NAME] 'h6 ' (Start: 1:21, End: 1:24)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:24, End: 1:25)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:25, End: 1:26)", tokensToString(tokens));
    }

    @Test
    public void testClassName() throws StyleFormatLexerException {
        String text = "h1.my-class {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_CLASS_NAME] 'my-class ' (Start: 1:4, End: 1:13)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:13, End: 1:14)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:14, End: 1:15)", tokensToString(tokens));
    }

    @Test
    public void testMultipleClassNames() throws StyleFormatLexerException {
        String text = "h1.my-class, image.left-aligned {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_CLASS_NAME] 'my-class' (Start: 1:4, End: 1:12)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:12, End: 1:14)\n" +
                "[BLOCK_START_NAME] 'image' (Start: 1:14, End: 1:19)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:19, End: 1:20)\n" +
                "[BLOCK_START_CLASS_NAME] 'left-aligned ' (Start: 1:20, End: 1:33)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:33, End: 1:34)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:34, End: 1:35)", tokensToString(tokens));
    }

    @Test
    public void testBlockStartOnMultipleLines() throws StyleFormatLexerException {
        String text = "h1.my-class,\n" +
                "image.left-aligned,\n" +
                "h3 {\n" +
                "}\n";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_CLASS_NAME] 'my-class' (Start: 1:4, End: 1:12)\n" +
                "[BLOCK_START_SEPARATOR] ',\n" +
                "' (Start: 1:12, End: 2:1)\n" +
                "[BLOCK_START_NAME] 'image' (Start: 2:1, End: 2:6)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 2:6, End: 2:7)\n" +
                "[BLOCK_START_CLASS_NAME] 'left-aligned' (Start: 2:7, End: 2:19)\n" +
                "[BLOCK_START_SEPARATOR] ',\n" +
                "' (Start: 2:19, End: 3:1)\n" +
                "[BLOCK_START_NAME] 'h3 ' (Start: 3:1, End: 3:4)\n" +
                "[BLOCK_OPEN] '{\n" +
                "' (Start: 3:4, End: 4:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 4:1, End: 4:2)\n" +
                "[IGNORE] '\n" +
                "' (Start: 4:2, End: 5:1)", tokensToString(tokens));
    }

    @Test
    public void testPseudoClass1() throws StyleFormatLexerException {
        String text = "h1:first-page{}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page' (Start: 1:4, End: 1:14)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:14, End: 1:15)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:15, End: 1:16)", tokensToString(tokens));
    }

    @Test
    public void testPseudoClass2() throws StyleFormatLexerException {
        String text = "h1:first-page {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page ' (Start: 1:4, End: 1:15)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:15, End: 1:16)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:16, End: 1:17)", tokensToString(tokens));
    }

    @Test
    public void testPseudoClassWith0Arguments() throws StyleFormatLexerException {
        String text = "h1:first-page() {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page' (Start: 1:4, End: 1:14)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:14, End: 1:15)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ') ' (Start: 1:15, End: 1:17)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:17, End: 1:18)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:18, End: 1:19)", tokensToString(tokens));
    }

    @Test
    public void testPseudoClassWith1Argument() throws StyleFormatLexerException {
        String text = "h1:first-page(42) {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page' (Start: 1:4, End: 1:14)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:14, End: 1:15)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '42' (Start: 1:15, End: 1:17)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ') ' (Start: 1:17, End: 1:19)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:19, End: 1:20)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:20, End: 1:21)", tokensToString(tokens));
    }

    @Test
    public void testPseudoClassWith2Arguments() throws StyleFormatLexerException {
        String text = "h1:first-page(1, 2) {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page' (Start: 1:4, End: 1:14)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:14, End: 1:15)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '1' (Start: 1:15, End: 1:16)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING_SEPARATOR] ', ' (Start: 1:16, End: 1:18)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '2' (Start: 1:18, End: 1:19)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ') ' (Start: 1:19, End: 1:21)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:21, End: 1:22)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:22, End: 1:23)", tokensToString(tokens));
    }

    @Test
    public void testPseudoClassAndClassName1() throws StyleFormatLexerException {
        String text = "h1.my-class:first-page {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_CLASS_NAME] 'my-class' (Start: 1:4, End: 1:12)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:12, End: 1:13)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page ' (Start: 1:13, End: 1:24)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:24, End: 1:25)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:25, End: 1:26)", tokensToString(tokens));
    }

    @Test
    public void testPseudoClassAndClassName2() throws StyleFormatLexerException {
        String text = "h1.my-class:page(2, 4) {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_CLASS_NAME] 'my-class' (Start: 1:4, End: 1:12)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:12, End: 1:13)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'page' (Start: 1:13, End: 1:17)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:17, End: 1:18)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '2' (Start: 1:18, End: 1:19)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING_SEPARATOR] ', ' (Start: 1:19, End: 1:21)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '4' (Start: 1:21, End: 1:22)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ') ' (Start: 1:22, End: 1:24)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:24, End: 1:25)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:25, End: 1:26)", tokensToString(tokens));
    }

    @Test
    public void testMultiplePseudoClasses() throws StyleFormatLexerException {
        String text = "h1.my-class:first-page(1), page:page(2), document {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_CLASS_NAME] 'my-class' (Start: 1:4, End: 1:12)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:12, End: 1:13)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page' (Start: 1:13, End: 1:23)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:23, End: 1:24)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '1' (Start: 1:24, End: 1:25)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ')' (Start: 1:25, End: 1:26)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:26, End: 1:28)\n" +
                "[BLOCK_START_NAME] 'page' (Start: 1:28, End: 1:32)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:32, End: 1:33)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'page' (Start: 1:33, End: 1:37)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:37, End: 1:38)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '2' (Start: 1:38, End: 1:39)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ')' (Start: 1:39, End: 1:40)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:40, End: 1:42)\n" +
                "[BLOCK_START_NAME] 'document ' (Start: 1:42, End: 1:51)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:51, End: 1:52)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:52, End: 1:53)", tokensToString(tokens));
    }

    @Test
    public void testMultiplePseudoClassesWithComment() throws StyleFormatLexerException {
        String text = "h1.my-class:first-page(1), /* Hello World */ page:page(2), document {}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_CLASS_NAME] 'my-class' (Start: 1:4, End: 1:12)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:12, End: 1:13)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page' (Start: 1:13, End: 1:23)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:23, End: 1:24)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '1' (Start: 1:24, End: 1:25)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ')' (Start: 1:25, End: 1:26)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:26, End: 1:28)\n" +
                "[MULTI_LINE_COMMENT] '/* Hello World */' (Start: 1:28, End: 1:45)\n" +
                "[BLOCK_START_SEPARATOR] ' ' (Start: 1:45, End: 1:46)\n" +
                "[BLOCK_START_NAME] 'page' (Start: 1:46, End: 1:50)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:50, End: 1:51)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'page' (Start: 1:51, End: 1:55)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:55, End: 1:56)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '2' (Start: 1:56, End: 1:57)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ')' (Start: 1:57, End: 1:58)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:58, End: 1:60)\n" +
                "[BLOCK_START_NAME] 'document ' (Start: 1:60, End: 1:69)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:69, End: 1:70)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:70, End: 1:71)", tokensToString(tokens));
    }

    @Test
    public void testComplexStyleBlockName() throws StyleFormatLexerException {
        String text = "h1, h2:page(1), h3.ok-well, // My single line comment\n" +
                "document, image.test:first-page() {\n" +
                "}\n";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:3, End: 1:5)\n" +
                "[BLOCK_START_NAME] 'h2' (Start: 1:5, End: 1:7)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:7, End: 1:8)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'page' (Start: 1:8, End: 1:12)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 1:12, End: 1:13)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTING] '1' (Start: 1:13, End: 1:14)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ')' (Start: 1:14, End: 1:15)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:15, End: 1:17)\n" +
                "[BLOCK_START_NAME] 'h3' (Start: 1:17, End: 1:19)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 1:19, End: 1:20)\n" +
                "[BLOCK_START_CLASS_NAME] 'ok-well' (Start: 1:20, End: 1:27)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 1:27, End: 1:29)\n" +
                "[SINGLE_LINE_COMMENT] '// My single line comment' (Start: 1:29, End: 1:54)\n" +
                "[BLOCK_START_SEPARATOR] '\n" +
                "' (Start: 1:54, End: 2:1)\n" +
                "[BLOCK_START_NAME] 'document' (Start: 2:1, End: 2:9)\n" +
                "[BLOCK_START_SEPARATOR] ', ' (Start: 2:9, End: 2:11)\n" +
                "[BLOCK_START_NAME] 'image' (Start: 2:11, End: 2:16)\n" +
                "[BLOCK_START_CLASS_SEPARATOR] '.' (Start: 2:16, End: 2:17)\n" +
                "[BLOCK_START_CLASS_NAME] 'test' (Start: 2:17, End: 2:21)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 2:21, End: 2:22)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page' (Start: 2:22, End: 2:32)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_START] '(' (Start: 2:32, End: 2:33)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SETTINGS_END] ') ' (Start: 2:33, End: 2:35)\n" +
                "[BLOCK_OPEN] '{\n" +
                "' (Start: 2:35, End: 3:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 3:1, End: 3:2)\n" +
                "[IGNORE] '\n" +
                "' (Start: 3:2, End: 4:1)", tokensToString(tokens));
    }

    @Test
    public void testSingleProperty() throws StyleFormatLexerException {
        String text = "h1 {\n" +
                "   font-family: Arial;\n" +
                "}\n";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1 ' (Start: 1:1, End: 1:4)\n" +
                "[BLOCK_OPEN] '{\n" +
                "   ' (Start: 1:4, End: 2:4)\n" +
                "[PROPERTY] 'font-family' (Start: 2:4, End: 2:15)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 2:15, End: 2:17)\n" +
                "[VALUE] 'Arial' (Start: 2:17, End: 2:22)\n" +
                "[VALUE_END] ';\n" +
                "' (Start: 2:22, End: 3:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 3:1, End: 3:2)\n" +
                "[IGNORE] '\n" +
                "' (Start: 3:2, End: 4:1)", tokensToString(tokens));
    }

    @Test
    public void testMultipleProperties() throws StyleFormatLexerException {
        String text = "h1 {\n" +
                "   font-family: Arial;\n" +
                "   font-size: 12pt;\n" +
                "   color: rgba(0.5, 0.5, 0.5, 1.0);\n" +
                "   \n" +
                "   another-property: \"yes\"\n" +
                "}\n";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1 ' (Start: 1:1, End: 1:4)\n" +
                "[BLOCK_OPEN] '{\n" +
                "   ' (Start: 1:4, End: 2:4)\n" +
                "[PROPERTY] 'font-family' (Start: 2:4, End: 2:15)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 2:15, End: 2:17)\n" +
                "[VALUE] 'Arial' (Start: 2:17, End: 2:22)\n" +
                "[VALUE_END] ';\n" +
                "   ' (Start: 2:22, End: 3:4)\n" +
                "[PROPERTY] 'font-size' (Start: 3:4, End: 3:13)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 3:13, End: 3:15)\n" +
                "[VALUE] '12pt' (Start: 3:15, End: 3:19)\n" +
                "[VALUE_END] ';\n" +
                "   ' (Start: 3:19, End: 4:4)\n" +
                "[PROPERTY] 'color' (Start: 4:4, End: 4:9)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 4:9, End: 4:11)\n" +
                "[VALUE] 'rgba(0.5, 0.5, 0.5, 1.0)' (Start: 4:11, End: 4:35)\n" +
                "[VALUE_END] ';\n" +
                "   \n" +
                "   ' (Start: 4:35, End: 6:4)\n" +
                "[PROPERTY] 'another-property' (Start: 6:4, End: 6:20)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': \"' (Start: 6:20, End: 6:23)\n" +
                "[VALUE] 'yes\"\n" +
                "' (Start: 6:23, End: 7:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 7:1, End: 7:2)\n" +
                "[IGNORE] '\n" +
                "' (Start: 7:2, End: 8:1)", tokensToString(tokens));
    }

    @Test
    public void testSinglePropertyInOneLine() throws StyleFormatLexerException {
        String text = "h1 {font-family: Arial}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1 ' (Start: 1:1, End: 1:4)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:4, End: 1:5)\n" +
                "[PROPERTY] 'font-family' (Start: 1:5, End: 1:16)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 1:16, End: 1:18)\n" +
                "[VALUE] 'Arial' (Start: 1:18, End: 1:23)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:23, End: 1:24)", tokensToString(tokens));
    }

    @Test
    public void testMultiplePropertiesInOneLine() throws StyleFormatLexerException {
        String text = "h1 {font-family: Arial; font-size: 12pt}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1 ' (Start: 1:1, End: 1:4)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:4, End: 1:5)\n" +
                "[PROPERTY] 'font-family' (Start: 1:5, End: 1:16)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 1:16, End: 1:18)\n" +
                "[VALUE] 'Arial' (Start: 1:18, End: 1:23)\n" +
                "[VALUE_END] '; ' (Start: 1:23, End: 1:25)\n" +
                "[PROPERTY] 'font-size' (Start: 1:25, End: 1:34)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 1:34, End: 1:36)\n" +
                "[VALUE] '12pt' (Start: 1:36, End: 1:40)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:40, End: 1:41)", tokensToString(tokens));
    }

    @Test
    public void testForgotNeededSemicolonForValueEnd() throws StyleFormatLexerException {
        String text = "h1 {font-family: Arial font-size: 12pt}";

        Assertions.assertThrows(StyleFormatLexerException.class, () -> tokenize(text));
    }

    @Test
    public void testWindowsNewLineSequence() throws StyleFormatLexerException {
        String text = "h1:first-page {\r\n" +
                "   font-family: Arial;\r\n" +
                "}\r\n";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page ' (Start: 1:4, End: 1:15)\n" +
                "[BLOCK_OPEN] '{\n" +
                "   ' (Start: 1:15, End: 2:4)\n" +
                "[PROPERTY] 'font-family' (Start: 2:4, End: 2:15)\n" +
                "[PROPERTY_VALUE_SEPARATOR] ': ' (Start: 2:15, End: 2:17)\n" +
                "[VALUE] 'Arial' (Start: 2:17, End: 2:22)\n" +
                "[VALUE_END] ';\n" +
                "' (Start: 2:22, End: 3:1)\n" +
                "[BLOCK_CLOSE] '}' (Start: 3:1, End: 3:2)\n" +
                "[IGNORE] '\n" +
                "' (Start: 3:2, End: 4:1)", tokensToString(tokens));
    }

    @Test
    public void testTabHandling() throws StyleFormatLexerException {
        String text = "h1:first-page\t{}";

        List<StyleFormatToken> tokens = tokenize(text);

        Assertions.assertEquals("[BLOCK_START_NAME] 'h1' (Start: 1:1, End: 1:3)\n" +
                "[BLOCK_START_PSEUDO_CLASS_SEPARATOR] ':' (Start: 1:3, End: 1:4)\n" +
                "[BLOCK_START_PSEUDO_CLASS_NAME] 'first-page ' (Start: 1:4, End: 1:15)\n" +
                "[BLOCK_OPEN] '{' (Start: 1:15, End: 1:16)\n" +
                "[BLOCK_CLOSE] '}' (Start: 1:16, End: 1:17)", tokensToString(tokens));
    }

}
