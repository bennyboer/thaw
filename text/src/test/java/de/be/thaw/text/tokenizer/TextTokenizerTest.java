package de.be.thaw.text.tokenizer;

import de.be.thaw.text.model.element.emphasis.TextEmphasis;
import de.be.thaw.text.tokenizer.exception.InvalidStateException;
import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.token.FormattedToken;
import de.be.thaw.text.tokenizer.token.ThingyToken;
import de.be.thaw.text.tokenizer.token.Token;
import de.be.thaw.text.tokenizer.token.TokenType;
import de.be.thaw.text.util.TextRange;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class TextTokenizerTest {

    @Test
    public void testSimple() throws TokenizeException {
        // Testing look ahead logic by using the ** bold modifier where a look ahead is necessary.
        String text = "Hello World!";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(1, tokens.size());
        Assertions.assertEquals("Hello World!", tokens.get(0).getValue());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals(new TextRange(0, text.length()), tokens.get(0).getRange());
    }

    @Test
    public void testSimpleMultiline() throws TokenizeException {
        // Testing look ahead logic by using the ** bold modifier where a look ahead is necessary.
        String text = "Hello World!\nI am a multi line string!\n\n\n\n\nWith multiple empty lines.";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hello World! I am a multi line string!", tokens.get(0).getValue());
        Assertions.assertEquals(new TextRange(0, 39), tokens.get(0).getRange());

        Assertions.assertEquals(TokenType.EMPTY_LINE, tokens.get(1).getType());
        Assertions.assertEquals(new TextRange(39, 44), tokens.get(1).getRange());

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("With multiple empty lines.", tokens.get(2).getValue());
        Assertions.assertEquals(new TextRange(44, text.length()), tokens.get(2).getRange());
    }

    @Test
    public void testItalicFormatting() throws TokenizeException {
        String text = "Hel*lo Wor*ld!";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextRange(0, 3), tokens.get(0).getRange());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("lo Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextRange(3, 11), tokens.get(1).getRange());
        Assertions.assertEquals(TextEmphasis.ITALIC, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextRange(11, 14), tokens.get(2).getRange());
    }

    @Test
    public void testBoldFormatting() throws TokenizeException {
        String text = "Hel**lo Wor**ld!";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextRange(0, 3), tokens.get(0).getRange());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("lo Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextRange(3, 13), tokens.get(1).getRange());
        Assertions.assertEquals(TextEmphasis.BOLD, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextRange(13, 16), tokens.get(2).getRange());
    }

    @Test
    public void testLookAhead() throws TokenizeException {
        // Testing look ahead logic by using the ** bold modifier where a look ahead is necessary.
        String text = "Hello **World**!";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

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

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextRange(0, 3), tokens.get(0).getRange());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("lo Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextRange(3, 11), tokens.get(1).getRange());
        Assertions.assertEquals(TextEmphasis.UNDERLINED, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextRange(11, 14), tokens.get(2).getRange());
    }

    @Test
    public void testCodeFormatting() throws TokenizeException {
        String text = "Hel`lo Wor`ld!";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextRange(0, 3), tokens.get(0).getRange());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("lo Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextRange(3, 11), tokens.get(1).getRange());
        Assertions.assertEquals(TextEmphasis.CODE, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextRange(11, 14), tokens.get(2).getRange());
    }

    @Test
    public void testCodeIsOverlayingEveryFormatting() throws TokenizeException {
        String text = "Hel**`l_o_ Wor`**ld!";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(3, tokens.size());
        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("Hel", tokens.get(0).getValue());
        Assertions.assertEquals(new TextRange(0, 3), tokens.get(0).getRange());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("l_o_ Wor", tokens.get(1).getValue());
        Assertions.assertEquals(new TextRange(5, 15), tokens.get(1).getRange());
        Assertions.assertEquals(1, ((FormattedToken) tokens.get(1)).getEmphases().size());
        Assertions.assertEquals(TextEmphasis.CODE, ((FormattedToken) tokens.get(1)).getEmphases().toArray()[0]);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(2).getType());
        Assertions.assertEquals("ld!", tokens.get(2).getValue());
        Assertions.assertEquals(new TextRange(17, 20), tokens.get(2).getRange());
    }

    @Test
    public void testComplexFormatting() throws TokenizeException {
        String text = "This is kind of a _*longer*_ text.\n" +
                "**There is a _lot of content_ in here!**\n" +
                "\n" +
                "Why would _***anyone***_ format **_*using*_** `UNDERLINE`?\n";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(TokenType.TEXT, tokens.get(0).getType());
        Assertions.assertEquals("This is kind of a ", tokens.get(0).getValue());
        Assertions.assertEquals(new TextRange(0, 18), tokens.get(0).getRange());

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(1).getType());
        Assertions.assertEquals("longer", tokens.get(1).getValue());
        Assertions.assertEquals(new TextRange(19, 27), tokens.get(1).getRange());
        Assertions.assertEquals(2, ((FormattedToken) tokens.get(1)).getEmphases().size());
        Assertions.assertTrue(((FormattedToken) tokens.get(1)).getEmphases().contains(TextEmphasis.UNDERLINED));
        Assertions.assertTrue(((FormattedToken) tokens.get(1)).getEmphases().contains(TextEmphasis.ITALIC));

        Assertions.assertEquals(TokenType.FORMATTED, tokens.get(8).getType());
        Assertions.assertEquals("anyone", tokens.get(8).getValue());
        Assertions.assertEquals(new TextRange(90, 98), tokens.get(8).getRange());
        Assertions.assertEquals(3, ((FormattedToken) tokens.get(8)).getEmphases().size());
        Assertions.assertTrue(((FormattedToken) tokens.get(8)).getEmphases().contains(TextEmphasis.UNDERLINED));
        Assertions.assertTrue(((FormattedToken) tokens.get(8)).getEmphases().contains(TextEmphasis.ITALIC));
        Assertions.assertTrue(((FormattedToken) tokens.get(8)).getEmphases().contains(TextEmphasis.BOLD));
    }

    @Test
    public void testIncompleteFormatting() throws TokenizeException {
        String text = "Hel`lo World!";

        TextTokenizer tt = new TextTokenizer();

        Assertions.assertThrows(InvalidStateException.class, () -> {
            tt.tokenize(new StringReader(text), token -> {
            });
        });
    }

    @Test
    public void testEscaping() throws TokenizeException {
        String[] texts = {"Hel\\`lo World!", "Hel\\*lo World!", "Hel\\_lo World!", "Hel\\#lo World!"};

        TextTokenizer tt = new TextTokenizer();

        for (String text : texts) {
            List<Token> tokens = new ArrayList<>();
            tt.tokenize(new StringReader(text), tokens::add);

            Assertions.assertEquals(1, tokens.size());
        }
    }

    @Test
    public void testEscapingInCode() throws TokenizeException {
        String text = "This is code: `System.out.println('This is code: \\`Test\\`')`";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals("System.out.println('This is code: `Test`')", tokens.get(1).getValue());
    }

    @Test
    public void testFakeEscape() throws TokenizeException {
        String text = "This is code: `System.out.println('This is code: \\Test\\')`";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals("System.out.println('This is code: \\Test\\')", tokens.get(1).getValue());
    }

    @Test
    public void testSimpleThingy() throws TokenizeException {
        String text = "#H1# First-level headline";

        TextTokenizer tt = new TextTokenizer();

        List<Token> tokens = new ArrayList<>();
        tt.tokenize(new StringReader(text), tokens::add);

        Assertions.assertEquals(2, tokens.size());
        Assertions.assertEquals(TokenType.THINGY, tokens.get(0).getType());
        Assertions.assertEquals(new TextRange(0, 4), tokens.get(0).getRange());

        ThingyToken token = (ThingyToken) tokens.get(0);
        Assertions.assertEquals("H1", token.getName());
        Assertions.assertEquals(0, token.getArguments().size());
        Assertions.assertEquals(0, token.getOptions().size());

        Assertions.assertEquals(TokenType.TEXT, tokens.get(1).getType());
        Assertions.assertEquals(new TextRange(4, text.length()), tokens.get(1).getRange());
    }

    @Test
    public void testThingyTokenizeError1() {
        String text = "#";

        TextTokenizer tt = new TextTokenizer();

        Assertions.assertThrows(InvalidStateException.class, () -> {
            tt.tokenize(new StringReader(text), token -> {
            });
        });
    }

    @Test
    public void testThingyTokenizeError2() {
        String text = "##";

        TextTokenizer tt = new TextTokenizer();

        Assertions.assertThrows(InvalidStateException.class, () -> {
            tt.tokenize(new StringReader(text), token -> {
            });
        });
    }

    @Test
    public void testThingyWithArguments() {
        // TODO
    }

    @Test
    public void testThingyWithOptionsNoArguments() {
        // TODO
    }

    @Test
    public void testThingyComplex() {
        // TODO Thingy test with multiple thingys + lines + formatting + ...
    }

    @Test
    public void testThingyInCodeBlock() {
        // TODO
    }

}
