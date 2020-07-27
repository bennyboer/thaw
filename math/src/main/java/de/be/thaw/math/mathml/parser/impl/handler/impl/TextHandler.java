package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.TextNode;
import org.w3c.dom.Node;

import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Handler for <mtext> MathML identifier nodes.
 */
public class TextHandler extends AbstractMathMLNodeParseHandler {

    public TextHandler() {
        super("mtext");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        String text = node.getTextContent();
        if (text.isBlank()) {
            throw new ParseException("Encountered an <mtext> node without content");
        }

        MathVariant mathVariant = MathVariant.NORMAL;
        if (text.length() == 1) {
            mathVariant = MathVariant.ITALIC; // Default for one character identifiers!
        }

        // Parse attribute from node
        Node mathVariantNode = node.getAttributes().getNamedItem("mathvariant");
        if (mathVariantNode != null) {
            mathVariant = MathVariant.forName(mathVariantNode.getTextContent()).orElseThrow(() -> new ParseException(String.format(
                    "Math variant name '%s' is unknown. Try one of the following: [%s]",
                    mathVariantNode.getTextContent(),
                    Arrays.stream(MathVariant.values()).sorted().map(MathVariant::getName).collect(Collectors.joining(", "))
            )));
        }

        // TODO Add attributes that we want to support (mathsize, ...)

        return new TextNode(text, mathVariant);
    }

}
