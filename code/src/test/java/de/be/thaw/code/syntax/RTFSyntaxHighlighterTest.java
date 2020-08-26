package de.be.thaw.code.syntax;

import de.be.thaw.code.syntax.exception.HighlightException;
import de.be.thaw.code.syntax.impl.RTFSyntaxHighlighter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class RTFSyntaxHighlighterTest {

    @Test
    public void simpleTest() throws HighlightException {
        RTFSyntaxHighlighter highlighter = new RTFSyntaxHighlighter();

        /*
        TEST IS ONLY TO BE EXECUTED IF PYTHON AND PYGMENTS ARE INSTALLED ON THE MACHINE.
         */

        boolean executeTest;
        try {
            highlighter.checkIfToolsAvailable();
            executeTest = true;
        } catch (HighlightException e) {
            executeTest = false;
        }

        if (executeTest) {
            String result = highlighter.highlight("public interface SyntaxHighlighter {\n" +
                    "   String highlight(String code, String language);\n" +
                    "}", "java", "colorful");

            Assertions.assertEquals("{\\rtf1\\ansi\\uc0\\deff0{\\fonttbl{\\f0\\fmodern\\fprq1\\fcharset0;}}{\\colortbl;\\red187\\green187\\blue187;\\red136\\green136\\blue136;\\red85\\green119\\blue153;\\red204\\green0\\blue0;\\red0\\green136\\blue0;\\red0\\green51\\blue136;\\red51\\green51\\blue153;\\red51\\green51\\blue51;\\red0\\green0\\blue0;\\red0\\green112\\blue32;\\red0\\green102\\blue187;\\red187\\green0\\blue102;\\red14\\green132\\blue181;\\red255\\green0\\blue0;\\red153\\green102\\blue51;\\red51\\green51\\blue187;\\red51\\green102\\blue153;\\red221\\green119\\blue0;\\red0\\green51\\blue102;\\red153\\green119\\blue0;\\red136\\green0\\blue0;\\red0\\green0\\blue204;\\red0\\green119\\blue0;\\red85\\green85\\blue85;\\red255\\green240\\blue240;\\red0\\green68\\blue221;\\red221\\green68\\blue34;\\red238\\green238\\blue238;\\red102\\green102\\blue102;\\red255\\green240\\blue255;\\red170\\green102\\blue0;\\red221\\green34\\blue0;\\red102\\green0\\blue238;\\red0\\green0\\blue221;\\red0\\green85\\blue136;\\red68\\green0\\blue238;\\red0\\green0\\blue128;\\red128\\green0\\blue128;\\red160\\green0\\blue0;\\red0\\green160\\blue0;\\red198\\green93\\blue9;\\red255\\green170\\blue170;}\\f0 {\\cf5\\b public} {\\cf5\\b interface} {\\cf12\\b SyntaxHighlighter} \\{\\par\n" +
                    "   String {\\cf11\\b highlight}(String code, String language);\\par\n" +
                    "\\}\\par\n" +
                    "}", result);
        }
    }

}
