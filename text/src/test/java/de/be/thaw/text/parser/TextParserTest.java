package de.be.thaw.text.parser;

import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.parser.exception.ParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

public class TextParserTest {

    private TextModel parse(String text) throws ParseException {
        return new TextParser().parse(new StringReader(text));
    }

    @Test
    public void testEmptyStringParse() throws ParseException {
        String text = "";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n", model.getRoot().toString());
    }

    @Test
    public void testSimpleFormatting1() throws ParseException {
        String text = "Hey `<Your Name>`";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'Hey '\n" +
                "    - [FORMATTED]: '<Your Name>' [CODE]\n", model.getRoot().toString());
    }

    @Test
    public void testSimpleFormatting2() throws ParseException {
        String text = "Hey *you*";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'Hey '\n" +
                "    - [FORMATTED]: 'you' [ITALIC]\n", model.getRoot().toString());
    }

    @Test
    public void testSimpleFormatting3() throws ParseException {
        String text = "Hey **you**";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'Hey '\n" +
                "    - [FORMATTED]: 'you' [BOLD]\n", model.getRoot().toString());
    }

    @Test
    public void testSimpleFormatting4() throws ParseException {
        String text = "Hey _you_";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'Hey '\n" +
                "    - [FORMATTED]: 'you' [UNDERLINED]\n", model.getRoot().toString());
    }

    @Test
    public void testComplexFormatting() throws ParseException {
        String text = "What *is **Love**?* Baby _don't_ hurt me!";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'What '\n" +
                "    - [FORMATTED]: 'is ' [ITALIC]\n" +
                "      - [FORMATTED]: 'Love' [BOLD, ITALIC]\n" +
                "    - [FORMATTED]: '?' [ITALIC]\n" +
                "    - [TEXT]: ' Baby '\n" +
                "    - [FORMATTED]: 'don't' [UNDERLINED]\n" +
                "    - [TEXT]: ' hurt me!'\n", model.getRoot().toString());
    }

    @Test
    public void testComplexExample() throws ParseException {
        String text = "#H1, label=headline# Headline\n" +
                "\n" +
                "Hallo Welt, dass ist ein kleines ***_Beispiel_***!\n" +
                "\n" +
                "- Erstens\n" +
                "- Zweitens ist das hier auch bold: **Hi!**\n" +
                "Test _Under*Ital**Bold**ic*line_ yay\n" +
                "- Drittens\n" +
                "\n" +
                "Another text with some _formatting applied *it* is **indeed** quite some ***formatting* around** here_.";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [THINGY]: Name: 'H1', Args: [], Options: {label=headline}\n" +
                "    - [TEXT]: ' Headline'\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'Hallo Welt, dass ist ein kleines '\n" +
                "    - [FORMATTED]: 'Beispiel' [BOLD, ITALIC, UNDERLINED]\n" +
                "    - [TEXT]: '!'\n" +
                "  - [BOX]: ~\n" +
                "    - [ENUMERATION]: Level: 1\n" +
                "      - [ENUMERATION_ITEM]: ~\n" +
                "        - [TEXT]: 'Erstens '\n" +
                "      - [ENUMERATION_ITEM]: ~\n" +
                "        - [TEXT]: 'Zweitens ist das hier auch bold: '\n" +
                "        - [FORMATTED]: 'Hi!' [BOLD]\n" +
                "        - [TEXT]: ' Test '\n" +
                "        - [FORMATTED]: 'Under' [UNDERLINED]\n" +
                "          - [FORMATTED]: 'Ital' [ITALIC, UNDERLINED]\n" +
                "            - [FORMATTED]: 'Bold' [BOLD, ITALIC, UNDERLINED]\n" +
                "          - [FORMATTED]: 'ic' [ITALIC, UNDERLINED]\n" +
                "        - [FORMATTED]: 'line' [UNDERLINED]\n" +
                "        - [TEXT]: ' yay '\n" +
                "      - [ENUMERATION_ITEM]: ~\n" +
                "        - [TEXT]: 'Drittens'\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'Another text with some '\n" +
                "    - [FORMATTED]: 'formatting applied ' [UNDERLINED]\n" +
                "      - [FORMATTED]: 'it' [ITALIC, UNDERLINED]\n" +
                "    - [FORMATTED]: ' is ' [UNDERLINED]\n" +
                "      - [FORMATTED]: 'indeed' [BOLD, UNDERLINED]\n" +
                "    - [FORMATTED]: ' quite some ' [UNDERLINED]\n" +
                "      - [FORMATTED]: 'formatting' [BOLD, ITALIC, UNDERLINED]\n" +
                "      - [FORMATTED]: ' around' [BOLD, UNDERLINED]\n" +
                "    - [FORMATTED]: ' here' [UNDERLINED]\n" +
                "    - [TEXT]: '.'\n", model.getRoot().toString());
    }

    @Test
    public void testMultiLineThingy() throws ParseException {
        String text = "#H1\n" +
                ",\n" +
                "label=headline\n" +
                "# Headline";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [THINGY]: Name: 'H1', Args: [], Options: {label=headline}\n" +
                "    - [TEXT]: ' Headline'\n", model.getRoot().toString());
    }

    @Test
    public void testNestedEnumeration() throws ParseException {
        String text = "- **Hello World**\n" +
                "  - I am nested\n" +
                "  - Me too!\n" +
                "- I am not..\n" +
                "  - Again nested\n" +
                "    - Even more nesting!";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [ENUMERATION]: Level: 1\n" +
                "      - [ENUMERATION_ITEM]: ~\n" +
                "        - [FORMATTED]: 'Hello World' [BOLD]\n" +
                "        - [TEXT]: ' '\n" +
                "      - [ENUMERATION]: Level: 2\n" +
                "        - [ENUMERATION_ITEM]: ~\n" +
                "          - [TEXT]: 'I am nested '\n" +
                "        - [ENUMERATION_ITEM]: ~\n" +
                "          - [TEXT]: 'Me too! '\n" +
                "      - [ENUMERATION_ITEM]: ~\n" +
                "        - [TEXT]: 'I am not.. '\n" +
                "      - [ENUMERATION]: Level: 2\n" +
                "        - [ENUMERATION_ITEM]: ~\n" +
                "          - [TEXT]: 'Again nested '\n" +
                "        - [ENUMERATION]: Level: 3\n" +
                "          - [ENUMERATION_ITEM]: ~\n" +
                "            - [TEXT]: 'Even more nesting!'\n", model.getRoot().toString());
    }

    @Test
    public void testFormattedWithThingyIncluded() throws ParseException {
        String text = "Dieses Dokument wurde zum #DATE, format='dd. MMMM yyyy'# um **#DATE, format='HH:mm:ss'#** erstellt.";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'Dieses Dokument wurde zum '\n" +
                "    - [THINGY]: Name: 'DATE', Args: [], Options: {format=dd. MMMM yyyy}\n" +
                "    - [TEXT]: ' um '\n" +
                "    - [FORMATTED]: '' [BOLD]\n" +
                "      - [THINGY]: Name: 'DATE', Args: [], Options: {format=HH:mm:ss}\n" +
                "    - [TEXT]: ' erstellt.'\n", model.getRoot().toString());
    }

    @Test
    public void testFormattedInLineText() throws ParseException {
        String text = "Test *.myClass*Hello world**";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: ~\n" +
                "  - [BOX]: ~\n" +
                "    - [TEXT]: 'Test '\n" +
                "    - [FORMATTED]: 'Hello world' [CUSTOM] (.myclass)\n", model.getRoot().toString());
    }

}
