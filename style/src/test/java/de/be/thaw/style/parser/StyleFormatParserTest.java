package de.be.thaw.style.parser;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.parser.exception.StyleModelParseException;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.nio.charset.StandardCharsets;

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

    @Test
    public void emptyBlockTest() throws StyleModelParseException {
        String text = "document {}";

        StyleModel model = parse(text);

        System.out.println("Test!");
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

        System.out.println("Test!");
    }

}
