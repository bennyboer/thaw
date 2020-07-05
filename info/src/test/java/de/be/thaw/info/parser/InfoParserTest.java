package de.be.thaw.info.parser;

import de.be.thaw.info.ThawInfo;
import de.be.thaw.info.model.language.Language;
import de.be.thaw.info.parser.exception.ParseException;
import de.be.thaw.info.parser.impl.DefaultInfoParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

public class InfoParserTest {

    @Test
    public void simpleParsingTest() throws ParseException {
        InfoParser parser = new DefaultInfoParser();

        ThawInfo info = parser.parse(new StringReader("encoding = UTF-8\n" +
                "language = de\n" +
                "\n" +
                "author.name = Benjamin Eder\n" +
                "author.email = mymail@myimaginarydomain.test\n"));

        Assertions.assertEquals(StandardCharsets.UTF_8, info.getEncoding());
        Assertions.assertEquals(Language.GERMAN, info.getLanguage());
        Assertions.assertEquals("Benjamin Eder", info.getAuthor().getName());
        Assertions.assertEquals("mymail@myimaginarydomain.test", info.getAuthor().getEmail());
    }

}
