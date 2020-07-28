package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.FractionNode;
import de.be.thaw.util.HorizontalAlignment;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler dealing with the fraction node.
 */
public class FractionHandler extends AbstractMathMLNodeParseHandler {

    public FractionHandler() {
        super("mfrac");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        // Parse bevelled attribute
        boolean bevelled = false;
        Node bevelledNode = node.getAttributes().getNamedItem("bevelled");
        if (bevelledNode != null) {
            bevelled = Boolean.parseBoolean(bevelledNode.getTextContent());
        }

        // Parse numalign (alignment of the numerator) attribute
        HorizontalAlignment numeratorAlignment = HorizontalAlignment.CENTER;
        Node numalignNode = node.getAttributes().getNamedItem("numalign");
        if (numalignNode != null) {
            try {
                numeratorAlignment = HorizontalAlignment.valueOf(numalignNode.getTextContent().toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new ParseException(String.format(
                        "The value '%s' of the attribute 'numalign' does not match any of the three allowed value 'center', 'left' or 'right'",
                        numalignNode.getTextContent()
                ));
            }
        }

        // Parse denomalign (alignment of the denominator) attribute
        HorizontalAlignment denominatorAlignment = HorizontalAlignment.CENTER;
        Node denomalignNode = node.getAttributes().getNamedItem("denomalign");
        if (denomalignNode != null) {
            try {
                denominatorAlignment = HorizontalAlignment.valueOf(denomalignNode.getTextContent().toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new ParseException(String.format(
                        "The value '%s' of the attribute 'denomalign' does not match any of the three allowed value 'center', 'left' or 'right'",
                        denomalignNode.getTextContent()
                ));
            }
        }

        // Parse linethickness attribute
        double lineThickness = ctx.getConfig().getDefaultLineThickness();
        Node linethicknessNode = node.getAttributes().getNamedItem("linethickness");
        if (linethicknessNode != null) {
            try {
                lineThickness = Double.parseDouble(linethicknessNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please provide only numbers as value of the linethickness attribute");
            }
        }

        MathMLNode fractionNode = new FractionNode(bevelled, numeratorAlignment, denominatorAlignment, lineThickness);

        List<Node> children = new ArrayList<>();
        int len = node.getChildNodes().getLength();
        for (int i = 0; i < len; i++) {
            Node child = node.getChildNodes().item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                children.add(child);
            }
        }

        // We expect exactly two child elements here!
        if (!node.hasChildNodes() || children.size() != 2) {
            throw new ParseException("A <mfrac> node is expected to have exactly 2 child nodes");
        }

        // Parse children
        for (Node child : children) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                MathMLNode childNode = MathMLParseContext.getParseHandler(child.getNodeName()).orElseThrow(() -> new ParseException(String.format(
                        "Could not find handler understanding how to deal with the '<%s>' MathML node",
                        child.getNodeName()
                ))).parse(child, ctx);

                fractionNode.addChild(childNode);
            }
        }

        return fractionNode;
    }

}
