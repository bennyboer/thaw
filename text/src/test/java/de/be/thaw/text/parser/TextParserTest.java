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

        Assertions.assertEquals("- [ROOT]: X\n" +
                "  - [BOX]: X\n", model.getRoot().toString());
    }

    @Test
    public void testSimpleFormatting1() throws ParseException {
        String text = "Hey `<Your Name>`";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: X\n" +
                "  - [BOX]: X\n" +
                "    - [TEXT]: [TEXT] [1:1 - 1:4], 'Hey '\n" +
                "    - [FORMATTED]: [FORMATTED] [1:5 - 1:17], '<Your Name>' > CODE\n", model.getRoot().toString());
    }

    @Test
    public void testSimpleFormatting2() throws ParseException {
        String text = "Hey *you*";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: X\n" +
                "  - [BOX]: X\n" +
                "    - [TEXT]: [TEXT] [1:1 - 1:4], 'Hey '\n" +
                "    - [FORMATTED]: [FORMATTED] [1:5 - 1:9], 'you' > ITALIC\n", model.getRoot().toString());
    }

    @Test
    public void testSimpleFormatting3() throws ParseException {
        String text = "Hey **you**";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: X\n" +
                "  - [BOX]: X\n" +
                "    - [TEXT]: [TEXT] [1:1 - 1:4], 'Hey '\n" +
                "    - [FORMATTED]: [FORMATTED] [1:5 - 1:11], 'you' > BOLD\n", model.getRoot().toString());
    }

    @Test
    public void testSimpleFormatting4() throws ParseException {
        String text = "Hey _you_";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: X\n" +
                "  - [BOX]: X\n" +
                "    - [TEXT]: [TEXT] [1:1 - 1:4], 'Hey '\n" +
                "    - [FORMATTED]: [FORMATTED] [1:5 - 1:9], 'you' > UNDERLINED\n", model.getRoot().toString());
    }

    @Test
    public void testComplexFormatting() throws ParseException {
        String text = "What *is **Love**?* Baby _don't_ hurt me!";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: X\n" +
                "  - [BOX]: X\n" +
                "    - [TEXT]: [TEXT] [1:1 - 1:5], 'What '\n" +
                "    - [FORMATTED]: [FORMATTED] [1:6 - 1:9], 'is ' > ITALIC\n" +
                "      - [FORMATTED]: [FORMATTED] [1:10 - 1:17], 'Love' > BOLD, ITALIC\n" +
                "    - [FORMATTED]: [FORMATTED] [1:18 - 1:19], '?' > ITALIC\n" +
                "    - [TEXT]: [TEXT] [1:20 - 1:25], ' Baby '\n" +
                "    - [FORMATTED]: [FORMATTED] [1:26 - 1:32], 'don't' > UNDERLINED\n" +
                "    - [TEXT]: [TEXT] [1:33 - 1:41], ' hurt me!'\n", model.getRoot().toString());
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

        Assertions.assertEquals("- [ROOT]: X\n" +
                "  - [BOX]: X\n" +
                "    - [THINGY]: [THINGY] [1:1 - 1:20], '#' > Name: 'H1', Args: [], Options: {label=headline}\n" +
                "    - [TEXT]: [TEXT] [1:21 - 1:29], ' Headline'\n" +
                "  - [BOX]: X\n" +
                "    - [TEXT]: [TEXT] [3:1 - 3:33], 'Hallo Welt, dass ist ein kleines '\n" +
                "    - [FORMATTED]: [FORMATTED] [3:37 - 3:46], 'Beispiel' > BOLD, ITALIC, UNDERLINED\n" +
                "    - [TEXT]: [TEXT] [3:50 - 3:50], '!'\n" +
                "  - [BOX]: X\n" +
                "    - [ENUMERATION]: X\n" +
                "      - [ENUMERATION_ITEM]: [ENUMERATION_ITEM_START] [5:1 - 5:2], '-'\n" +
                "        - [TEXT]: [TEXT] [5:3 - 5:10], 'Erstens '\n" +
                "      - [ENUMERATION_ITEM]: [ENUMERATION_ITEM_START] [6:1 - 6:2], '-'\n" +
                "        - [TEXT]: [TEXT] [6:3 - 6:35], 'Zweitens ist das hier auch bold: '\n" +
                "        - [FORMATTED]: [FORMATTED] [6:36 - 6:42], 'Hi!' > BOLD\n" +
                "        - [TEXT]: [TEXT] [6:43 - 7:5], ' Test '\n" +
                "        - [FORMATTED]: [FORMATTED] [7:6 - 7:11], 'Under' > UNDERLINED\n" +
                "          - [FORMATTED]: [FORMATTED] [7:12 - 7:16], 'Ital' > ITALIC, UNDERLINED\n" +
                "            - [FORMATTED]: [FORMATTED] [7:17 - 7:24], 'Bold' > BOLD, ITALIC, UNDERLINED\n" +
                "          - [FORMATTED]: [FORMATTED] [7:25 - 7:27], 'ic' > ITALIC, UNDERLINED\n" +
                "        - [FORMATTED]: [FORMATTED] [7:28 - 7:32], 'line' > UNDERLINED\n" +
                "        - [TEXT]: [TEXT] [7:33 - 7:37], ' yay '\n" +
                "      - [ENUMERATION_ITEM]: [ENUMERATION_ITEM_START] [8:1 - 8:2], '-'\n" +
                "        - [TEXT]: [TEXT] [8:3 - 8:10], 'Drittens'\n" +
                "  - [BOX]: X\n" +
                "    - [TEXT]: [TEXT] [10:1 - 10:23], 'Another text with some '\n" +
                "    - [FORMATTED]: [FORMATTED] [10:24 - 10:43], 'formatting applied ' > UNDERLINED\n" +
                "      - [FORMATTED]: [FORMATTED] [10:44 - 10:47], 'it' > ITALIC, UNDERLINED\n" +
                "    - [FORMATTED]: [FORMATTED] [10:48 - 10:51], ' is ' > UNDERLINED\n" +
                "      - [FORMATTED]: [FORMATTED] [10:52 - 10:61], 'indeed' > BOLD, UNDERLINED\n" +
                "    - [FORMATTED]: [FORMATTED] [10:62 - 10:73], ' quite some ' > UNDERLINED\n" +
                "      - [FORMATTED]: [FORMATTED] [10:76 - 10:87], 'formatting' > BOLD, ITALIC, UNDERLINED\n" +
                "      - [FORMATTED]: [FORMATTED] [10:88 - 10:96], ' around' > BOLD, UNDERLINED\n" +
                "    - [FORMATTED]: [FORMATTED] [10:97 - 10:102], ' here' > UNDERLINED\n" +
                "    - [TEXT]: [TEXT] [10:103 - 10:103], '.'\n", model.getRoot().toString());
    }

    @Test
    public void testMultiLineThingy() throws ParseException {
        String text = "#H1\n" +
                ",\n" +
                "label=headline\n" +
                "# Headline";

        TextModel model = parse(text);

        Assertions.assertEquals("- [ROOT]: X\n" +
                "  - [BOX]: X\n" +
                "    - [THINGY]: [THINGY] [1:1 - 4:1], '#' > Name: 'H1', Args: [], Options: {label=headline}\n" +
                "    - [TEXT]: [TEXT] [4:2 - 4:10], ' Headline'\n", model.getRoot().toString());
    }

}
