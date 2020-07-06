package de.be.thaw.text.tokenizer;

import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.token.EnumerationItemStartToken;
import de.be.thaw.text.tokenizer.token.FormattedToken;
import de.be.thaw.text.tokenizer.token.ThingyToken;
import de.be.thaw.text.tokenizer.token.Token;
import de.be.thaw.text.tokenizer.token.TokenType;
import de.be.thaw.text.tokenizer.util.result.Result;
import de.be.thaw.text.util.TextPosition;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class TextTokenizerTest {

    /**
     * Helper method to tokenize the passed string.
     *
     * @param s to tokenize
     * @return the list of tokens
     * @throws TokenizeException in case tokenizing went wrong
     */
    private List<Token> tokenize(String s) throws TokenizeException {
        TextTokenizer tt = new TextTokenizer(new StringReader(s));

        List<Token> result = new ArrayList<>();
        while (tt.hasNext()) {
            Result<Token, TokenizeException> r = tt.next();

            if (r.isError()) {
                throw r.error();
            } else {
                result.add(r.result());
            }
        }
        return result;
    }

    @Test
    public void testSimple() throws TokenizeException {
        // Testing look ahead logic by using the ** bold modifier where a look ahead is necessary.
        String text = "Hello World!";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(1, tokens.size());
        Assertions.assertEquals("Hello World!", tokens.get(0).getValue());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 12), tokens.get(0).getPosition());
    }

    @Test
    public void testSimpleMultiline() throws TokenizeException {
        // Testing look ahead logic by using the ** bold modifier where a look ahead is necessary.
        String text = "Hello World!\n" +
                "I am a multi line string!\n" +
                "\n" +
                "\n" +
                "\n" +
                "\n" +
                "With multiple empty lines.";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hello World! I am a multi line string!", tokens.get(0).getValue());
        Assertions.assertEquals(new TextPosition(1, 1, 2, 25), tokens.get(0).getPosition());

        Assertions.assertEquals(TokenType.EMPTY_LINE, tokens.get(1).getType());
        Assertions.assertEquals(new TextPosition(2, 26, 6, 1), tokens.get(1).getPosition());

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("With multiple empty lines.", tokens.get(2).getValue());
        Assertions.assertEquals(new TextPosition(7, 1, 7, 26), tokens.get(2).getPosition());
    }

    @Test
    public void testItalicFormatting() throws TokenizeException {
        String text = "Hel*lo Wor*ld!";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 3), tokens.get(0).getPosition());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("lo Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(1, 4, 1, 11), tokens.get(1).getPosition());
        Assertions.assertEquals(TextEmphasis.ITALIC, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextPosition(1, 12, 1, 14), tokens.get(2).getPosition());
    }

    @Test
    public void testItalicFormatting2() throws TokenizeException {
        String text = "Hey *you*";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());
    }

    @Test
    public void testItalicFormatting3() throws TokenizeException {
        String text = "*Hey* you";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());
    }

    @Test
    public void testBoldFormatting() throws TokenizeException {
        String text = "Hel**lo Wor**ld!";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 3), tokens.get(0).getPosition());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("lo Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(1, 4, 1, 13), tokens.get(1).getPosition());
        Assertions.assertEquals(TextEmphasis.BOLD, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextPosition(1, 14, 1, 16), tokens.get(2).getPosition());
    }

    @Test
    public void testLookAhead() throws TokenizeException {
        // Testing look ahead logic by using the ** bold modifier where a look ahead is necessary.
        String text = "Hello **World**!";

        List<Token> tokens = tokenize(text);

        Token token = tokens.get(1);
        Assertions.assertTrue(token instanceof FormattedToken);

        FormattedToken ft = (FormattedToken) token;
        Assertions.assertEquals("World", ft.getValue());
        Assertions.assertEquals(TextEmphasis.BOLD, ft.getEmphases().toArray()[0]);

        Assertions.assertEquals("!", tokens.get(2).getValue());
    }

    @Test
    public void testUnderlineFormatting() throws TokenizeException {
        String text = "Hel_lo Wor_ld!";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 3), tokens.get(0).getPosition());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("lo Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(1, 4, 1, 11), tokens.get(1).getPosition());
        Assertions.assertEquals(TextEmphasis.UNDERLINED, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextPosition(1, 12, 1, 14), tokens.get(2).getPosition());
    }

    @Test
    public void testCodeFormatting() throws TokenizeException {
        String text = "Hel`lo Wor`ld!";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 3), tokens.get(0).getPosition());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("lo Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(1, 4, 1, 11), tokens.get(1).getPosition());
        Assertions.assertEquals(TextEmphasis.CODE, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextPosition(1, 12, 1, 14), tokens.get(2).getPosition());
    }

    @Test
    public void testCodeIsOverlayingEveryFormatting() throws TokenizeException {
        String text = "Hel**`l_o_ Wor`**ld!";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 3), tokens.get(0).getPosition());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("l_o_ Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(1, 6, 1, 15), tokens.get(1).getPosition());
        Assertions.assertEquals(1, ((FormattedToken) tokens.get(1)).getEmphases().size());
        Assertions.assertEquals(TextEmphasis.CODE, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextPosition(1, 18, 1, 20), tokens.get(2).getPosition());
    }

    @Test
    public void testComplexFormatting() throws TokenizeException {
        String text = "This is kind of a _*longer*_ text.\n" +
                "**There is a _lot of content_ in here!**\n" +
                "\n" +
                "Why would _***anyone***_ format **_*using*_** `UNDERLINE`?\n";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("This is kind of a ", tokens.get(0).getValue());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 18), tokens.get(0).getPosition());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("longer", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(1, 20, 1, 27), tokens.get(1).getPosition());
        Assertions.assertEquals(2, ((FormattedToken) tokens.get(1)).getEmphases().size());
        Assertions.assertTrue(((FormattedToken) tokens.get(1)).getEmphases().contains(TextEmphasis.UNDERLINED));
        Assertions.assertTrue(((FormattedToken) tokens.get(1)).getEmphases().contains(TextEmphasis.ITALIC));

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(8).getType());
        Assertions.assertEquals("anyone", tokens.get(8).getValue());
        Assertions.assertEquals(new TextPosition(4, 14, 4, 21), tokens.get(8).getPosition());
        Assertions.assertEquals(3, ((FormattedToken) tokens.get(8)).getEmphases().size());
        Assertions.assertTrue(((FormattedToken) tokens.get(8)).getEmphases().contains(TextEmphasis.UNDERLINED));
        Assertions.assertTrue(((FormattedToken) tokens.get(8)).getEmphases().contains(TextEmphasis.ITALIC));
        Assertions.assertTrue(((FormattedToken) tokens.get(8)).getEmphases().contains(TextEmphasis.BOLD));
    }

    @Test
    public void testIncompleteFormatting() {
        String text = "Hel`lo World!";

        Assertions.assertThrows(TokenizeException.class, () -> tokenize(text));
    }

    @Test
    public void testEscaping() throws TokenizeException {
        String[] texts = {"Hel\\`lo World!", "Hel\\*lo World!", "Hel\\_lo World!", "Hel\\#lo World!"};

        for (String text : texts) {
            List<Token> tokens = tokenize(text);

            Assertions.assertEquals(1, tokens.size());
        }
    }

    @Test
    public void testEscapingInCode() throws TokenizeException {
        String text = "This is code: `System.out.println('This is code: \\`Test\\`')`";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals("System.out.println('This is code: `Test`')", tokens.get(1).getValue());
    }

    @Test
    public void testFakeEscape() throws TokenizeException {
        String text = "This is code: `System.out.println('This is code: \\Test\\')`";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals("System.out.println('This is code: \\Test\\')", tokens.get(1).getValue());
    }

    @Test
    public void testComplexFormatting2() throws TokenizeException {
        String text = "***_Example_***";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(1, tokens.size());
        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(0).getType());
        Assertions.assertEquals("Example", tokens.get(0).getValue());
    }

    @Test
    public void testComplexFormatting3() throws TokenizeException {
        String text = "_Under*Ital**Bold**ic*line_";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(5, tokens.size());
    }

    @Test
    public void testSimpleThingy() throws TokenizeException {
        String text = "#H1# First-level headline";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals(TokenType.THINGY, tokens.get(0).getType());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 4), tokens.get(0).getPosition());

        ThingyToken token = (ThingyToken) tokens.get(0);
        Assertions.assertEquals("H1", token.getName());
        Assertions.assertEquals(0, token.getArguments().size());
        Assertions.assertEquals(0, token.getOptions().size());

        Assertions.assertEquals(TokenType.TEXT, tokens.get(1).getType());
        Assertions.assertEquals(new TextPosition(1, 5, 1, 25), tokens.get(1).getPosition());
    }

    @Test
    public void testThingyTokenizeError1() {
        String text = "#";

        Assertions.assertThrows(TokenizeException.class, () -> tokenize(text));
    }

    @Test
    public void testThingyTokenizeError2() {
        String text = "##";

        Assertions.assertThrows(TokenizeException.class, () -> tokenize(text));
    }

    @Test
    public void testThingyWithArguments() throws TokenizeException {
        String text = "#H1, first-level-heading, 3, 42, hey there, \" hey there\"# First-level headline";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());

        Assertions.assertEquals(TokenType.THINGY, tokens.get(0).getType());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 57), tokens.get(0).getPosition());

        ThingyToken thingyToken = (ThingyToken) tokens.get(0);
        Assertions.assertEquals("H1", thingyToken.getName());
        Assertions.assertEquals(Arrays.asList("first-level-heading", "3", "42", "hey there", "\" hey there\""), thingyToken.getArguments());
        Assertions.assertEquals(0, thingyToken.getOptions().size());

        Assertions.assertEquals(" First-level headline", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(1, 58, 1, 78), tokens.get(1).getPosition());
    }

    @Test
    public void testThingyWithOptionsNoArguments() throws TokenizeException {
        String text = "#H1, label=first-level-headline, hey = there, key = \"hello world\"# First-level headline";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());

        Assertions.assertEquals(TokenType.THINGY, tokens.get(0).getType());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 66), tokens.get(0).getPosition());

        ThingyToken thingyToken = (ThingyToken) tokens.get(0);
        Assertions.assertEquals("H1", thingyToken.getName());

        Assertions.assertEquals(0, thingyToken.getArguments().size());

        Assertions.assertEquals("first-level-headline", thingyToken.getOptions().get("label"));
        Assertions.assertEquals("there", thingyToken.getOptions().get("hey"));
        Assertions.assertEquals("\"hello world\"", thingyToken.getOptions().get("key"));

        Assertions.assertEquals(" First-level headline", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(1, 67, 1, 87), tokens.get(1).getPosition());
    }

    @Test
    public void testThingyComplex() throws TokenizeException {
        String text = "#H1, label=headline# Headline\n" +
                "\n" +
                "I am a *simple* _paragraph_ with some code `COOOOODE`.\n" +
                "Additionally I feature a lot of ***_formatting_*** to please the eye.\n" +
                "\n" +
                "#IMG, src=\"C:\\Users\\YOURNAME\\cool_image.png\", width=600, height=250, alignment = CENTER#\n";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(14, tokens.size());

        ThingyToken h1Thingy = (ThingyToken) tokens.get(0);
        Assertions.assertEquals("H1", h1Thingy.getName());
        Assertions.assertEquals(1, h1Thingy.getOptions().size());

        ThingyToken imgThingy = (ThingyToken) tokens.get(13);
        Assertions.assertEquals("IMG", imgThingy.getName());
        Assertions.assertEquals(0, imgThingy.getArguments().size());
        Assertions.assertEquals(4, imgThingy.getOptions().size());
        Assertions.assertEquals("\"C:\\Users\\YOURNAME\\cool_image.png\"", imgThingy.getOptions().get("src"));
        Assertions.assertEquals("600", imgThingy.getOptions().get("width"));
        Assertions.assertEquals("250", imgThingy.getOptions().get("height"));
        Assertions.assertEquals("CENTER", imgThingy.getOptions().get("alignment"));
    }

    @Test
    public void testThingyInCodeBlock() throws TokenizeException {
        String text = "I am a code block with a thingy in me: `#H1, label=test#`";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("#H1, label=test#", tokens.get(1).getValue());
    }

    @Test
    public void testMultiLineThingy() throws TokenizeException {
        String text = "#H1\n" +
                ",\n" +
                "label=headline\n" +
                "# Headline\n";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals(new TextPosition(1, 1, 4, 1), tokens.get(0).getPosition());
    }

    @Test
    public void testSimpleEnumeration() throws TokenizeException {
        String text = "Hello world\n" +
                "- Hello\n" +
                "  - World";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(5, tokens.size());

        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hello world ", tokens.get(0).getValue());
        Assertions.assertEquals(new TextPosition(1, 1, 1, 12), tokens.get(0).getPosition());

        Assertions.assertEquals(TokenType.ENUMERATION_ITEM_START, tokens.get(1).getType());
        Assertions.assertEquals("-", tokens.get(1).getValue());
        Assertions.assertEquals(new TextPosition(2, 1, 2, 2), tokens.get(1).getPosition());
        Assertions.assertEquals(0, ((EnumerationItemStartToken) tokens.get(1)).getIndent());

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("Hello ", tokens.get(2).getValue());
        Assertions.assertEquals(new TextPosition(2, 3, 2, 8), tokens.get(2).getPosition());

        Assertions.assertEquals(TokenType.ENUMERATION_ITEM_START, tokens.get(3).getType());
        Assertions.assertEquals("-", tokens.get(3).getValue());
        Assertions.assertEquals(new TextPosition(3, 1, 3, 4), tokens.get(3).getPosition());
        Assertions.assertEquals(2, ((EnumerationItemStartToken) tokens.get(3)).getIndent());

        Assertions.assertEquals(TokenType.TEXT, tokens.get(4).getType());
        Assertions.assertEquals("World", tokens.get(4).getValue());
        Assertions.assertEquals(new TextPosition(3, 5, 3, 9), tokens.get(4).getPosition());
    }

    @Test
    public void testComplexEnumeration() throws TokenizeException {
        String text = "#H1, label=headline# A complex enumeration\n" +
                "\n" +
                "The following enumeration is complex!\n" +
                "- A\n" +
                "- B\n" +
                "  - a)\n" +
                "  - b)\n" +
                "- C\n" +
                "  - a)\n" +
                "    - 1.\n" +
                "    - 2.\n" +
                "  - b)\n";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(22, tokens.size());

        EnumerationItemStartToken token = (EnumerationItemStartToken) tokens.get(18);

        Assertions.assertEquals(TokenType.ENUMERATION_ITEM_START, token.getType());
        Assertions.assertEquals(4, token.getIndent());
    }

    @Test
    public void testComplexEnumeration2() throws TokenizeException {
        String text = "- A\n" +
                "\t- B\n" +
                "\t\t- C\n" +
                "\t\t\t- D\n" +
                "\t\t\t\t- E\n" +
                "\t\t\t\t\t- F\n" +
                "- G";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(14, tokens.size());

        EnumerationItemStartToken token = (EnumerationItemStartToken) tokens.get(10);
        Assertions.assertEquals(TokenType.ENUMERATION_ITEM_START, token.getType());
        Assertions.assertEquals(5, token.getIndent());

        token = (EnumerationItemStartToken) tokens.get(12);
        Assertions.assertEquals(TokenType.ENUMERATION_ITEM_START, token.getType());
        Assertions.assertEquals(0, token.getIndent());
    }

    @Test
    public void testNoEnumeration() throws TokenizeException {
        String text = "The following enumeration is no enumeration!\n" +
                "-A\n" +
                "-B\n";

        List<Token> tokens = tokenize(text);

        Assertions.assertEquals(1, tokens.size());
    }

}
