package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SubscriptNode;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler dealing with the superscript node.
 */
public class SubscriptHandler extends AbstractMathMLNodeParseHandler {

    /**
     * The default subscript shift.
     * 0.5 means 50 % below the baseline.
     */
    static final double DEFAULT_SUBSCRIPT_SHIFT = 0.7;

    public SubscriptHandler() {
        super("msub");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        // Parse subscriptshift attribute
        double subscriptShift = DEFAULT_SUBSCRIPT_SHIFT;
        Node subscriptshiftNode = node.getAttributes().getNamedItem("subscriptshift");
        if (subscriptshiftNode != null) {
            try {
                subscriptShift = Double.parseDouble(subscriptshiftNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please only specify a number without unit in the subscriptshift attribute of a <msub> node");
            }
        }

        SubscriptNode subscriptNode = new SubscriptNode(subscriptShift);

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
            throw new ParseException("A <msub> node is expected to have exactly 2 child nodes");
        }

        // Parse children
        for (Node child : children) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                MathMLNode childNode = MathMLParseContext.getParseHandler(child.getNodeName()).orElseThrow(() -> new ParseException(String.format(
                        "Could not find handler understanding how to deal with the '<%s>' MathML node",
                        child.getNodeName()
                ))).parse(child, ctx);

                subscriptNode.addChild(childNode);
            }
        }

        return subscriptNode;
    }

}
