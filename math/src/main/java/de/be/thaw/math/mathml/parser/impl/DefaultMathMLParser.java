package de.be.thaw.math.mathml.parser.impl;

import de.be.thaw.math.mathml.parser.MathMLParser;
import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;

/**
 * Default implementation of the MathML parser.
 */
public class DefaultMathMLParser implements MathMLParser {

    @Override
    public MathMLTree parse(InputStream stream) throws ParseException {
        try {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setFeature(XMLConstants.FEATURE_SECURE_PROCESSING, true);

            DocumentBuilder builder = factory.newDocumentBuilder();
            org.w3c.dom.Document document = builder.parse(stream);

            if (document.getChildNodes().getLength() != 1) {
                throw new ParseException("Expected exactly 1 <math> element as root node of the MathML source text");
            }

            Node rootNode = document.getChildNodes().item(0);
            if (!rootNode.getNodeName().equals("math")) {
                throw new ParseException("Expected the root node to have the name 'math'");
            }

            MathMLParseContext ctx = new MathMLParseContext();

            MathMLNodeParseHandler parseHandler = MathMLParseContext.getParseHandler(rootNode.getNodeName()).orElseThrow();
            MathMLNode root = parseHandler.parse(rootNode, ctx);

            return new MathMLTree(root);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            throw new ParseException(e);
        }
    }

}
