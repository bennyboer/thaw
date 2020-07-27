package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SuperscriptNode;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler dealing with the superscript node.
 */
public class SuperscriptHandler extends AbstractMathMLNodeParseHandler {

    /**
     * The default superscript shift.
     * 0.5 means 50 % above the baseline.
     */
    private static final double DEFAULT_SUPERSCRIPT_SHIFT = 0.6;

    public SuperscriptHandler() {
        super("msup");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        // Parse superscriptshift attribute
        double superscriptShift = DEFAULT_SUPERSCRIPT_SHIFT;
        Node superscriptshiftNode = node.getAttributes().getNamedItem("superscriptshift");
        if (superscriptshiftNode != null) {
            try {
                superscriptShift = Double.parseDouble(superscriptshiftNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please only specify a number without unit in the superscriptshift attribute of a <msup> node");
            }
        }

        SuperscriptNode superscriptNode = new SuperscriptNode(superscriptShift);

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
            throw new ParseException("A <msup> node is expected to have exactly 2 child nodes");
        }

        // Parse children
        for (Node child : children) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                MathMLNode childNode = MathMLParseContext.getParseHandler(child.getNodeName()).orElseThrow(() -> new ParseException(String.format(
                        "Could not find handler understanding how to deal with the '<%s>' MathML node",
                        child.getNodeName()
                ))).parse(child, ctx);

                superscriptNode.addChild(childNode);
            }
        }

        return superscriptNode;
    }

}
