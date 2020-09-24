package de.be.thaw.style.parser;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.parser.exception.StyleModelParseException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public class StyleFormatParserTest {

    /**
     * Parse the passed text in Thaw style format.
     *
     * @param text to parse
     * @return the parsed style model
     */
    private StyleModel parse(String text) throws StyleModelParseException {
        return StyleFormatParserFactory.getInstance().getParser().parse(new StringReader(text));
    }

    /**
     * Convert the passed style model to a string.
     *
     * @param model to convert
     * @return the result
     */
    private String styleModelToString(StyleModel model) {
        StringBuilder sb = new StringBuilder();

        model.getBlocks().stream()
                .sorted(Comparator.comparing(o -> o.getSelector().toString()))
                .forEach(block -> {
                    sb.append(block.toString());
                    sb.append('\n');
                });

        return sb.toString();
    }

    @Test
    public void emptyBlockTest() throws StyleModelParseException {
        String text = "document {}";

        StyleModel model = parse(text);

        Assertions.assertEquals("document {\n" +
                "}\n", styleModelToString(model));
    }

    @Test
    public void complexTest() throws StyleModelParseException, IOException {
        StyleModel model;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(
                StyleFormatParserTest.class.getResourceAsStream("/test-files/complex.tds"),
                StandardCharsets.UTF_8
        ))) {
            model = StyleFormatParserFactory.getInstance().getParser().parse(br);
        }

        Assertions.assertEquals("document {\n" +
                "\tbackground-color: rgba(0.917647, 0.917647, 0.917647, 1.000000);\n" +
                "\tcolor: rgba(0.200000, 0.200000, 0.200000, 1.000000);\n" +
                "\tfont-family: url(my-cool-font.ttf);\n" +
                "\tfont-kerning: native;\n" +
                "\tfont-size: 12.000000pt;\n" +
                "\tfont-variant: plain;\n" +
                "\theight: 297.000000mm;\n" +
                "\tinline-code-font-family: 'Consolas';\n" +
                "\tline-height: 1.000000;\n" +
                "\tmargin-bottom: 0.000000mm;\n" +
                "\tmargin-left: 0.000000mm;\n" +
                "\tmargin-right: 0.000000mm;\n" +
                "\tmargin-top: 0.000000mm;\n" +
                "\tpadding-bottom: 0.000000mm;\n" +
                "\tpadding-left: 0.000000mm;\n" +
                "\tpadding-right: 0.000000mm;\n" +
                "\tpadding-top: 0.000000mm;\n" +
                "\ttext-align: center;\n" +
                "\ttext-justify: true;\n" +
                "\twidth: 210.000000mm;\n" +
                "}\n" +
                "enumeration {\n" +
                "\tmargin-left: 10.000000mm;\n" +
                "}\n" +
                "enumeration:level(2) {\n" +
                "\tcolor: rgba(1.000000, 0.000000, 0.000000, 1.000000);\n" +
                "\tlist-style-type: circle;\n" +
                "\tmargin-bottom: 10.000000mm;\n" +
                "\tmargin-left: 10.000000mm;\n" +
                "\tmargin-right: 10.000000mm;\n" +
                "\tmargin-top: 10.000000mm;\n" +
                "}\n" +
                "h {\n" +
                "\tcounter-style: decimal;\n" +
                "\tnumbering: \"%parent-heading%.%level-counter%\";\n" +
                "}\n" +
                "h.appendix {\n" +
                "\tcounter-style: lower_latin;\n" +
                "\tnumbering: \"%parent-heading%.%level-counter%\";\n" +
                "}\n" +
                "h1 {\n" +
                "\tfont-family: Calibri;\n" +
                "}\n" +
                "h1.appendix {\n" +
                "\tcounter-style: upper_latin;\n" +
                "\tnumbering: \"%level-counter%\";\n" +
                "}\n" +
                "h2 {\n" +
                "\tfont-family: Calibri;\n" +
                "}\n" +
                "h3 {\n" +
                "\tfont-family: Calibri;\n" +
                "}\n" +
                "h4 {\n" +
                "\tfont-family: Calibri;\n" +
                "}\n" +
                "h5 {\n" +
                "\tfont-family: Calibri;\n" +
                "}\n" +
                "h6 {\n" +
                "\tfont-family: Calibri;\n" +
                "}\n" +
                "image {\n" +
                "\tborder: 1px solid #000000;\n" +
                "\tmargin-bottom: 5.000000mm;\n" +
                "\tmargin-top: 5.000000mm;\n" +
                "}\n" +
                "image.special-class {\n" +
                "\tmargin-left: 5.000000mm;\n" +
                "}\n" +
                "page {\n" +
                "\tfooter: \"my-footer-folder\";\n" +
                "\theader: \"my-header-folder\";\n" +
                "}\n" +
                "page:page(end=5) {\n" +
                "}\n" +
                "paragraph {\n" +
                "}\n" +
                "style.highlighted {\n" +
                "\tcolor: rgba(1.000000, 0.000000, 0.000000, 1.000000);\n" +
                "\tfont-variant: bold;\n" +
                "\tmargin-bottom: 100.000000mm;\n" +
                "\tmargin-left: 100.000000mm;\n" +
                "\tmargin-right: 100.000000mm;\n" +
                "\tmargin-top: 100.000000mm;\n" +
                "}\n" +
                "toc {\n" +
                "\tfill: dotted;\n" +
                "\tmargin-left: 5.000000mm;\n" +
                "}\n" +
                "toc:level(2) {\n" +
                "\tfont-variant: italic;\n" +
                "}\n", styleModelToString(model));
    }

}
