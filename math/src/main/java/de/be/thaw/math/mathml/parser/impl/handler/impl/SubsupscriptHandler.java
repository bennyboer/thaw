package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SubsuperscriptNode;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler dealing with the subsupscript node.
 */
public class SubsupscriptHandler extends AbstractMathMLNodeParseHandler {

    public SubsupscriptHandler() {
        super("msubsup");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        // Parse subscriptshift attribute
        double subscriptShift = SubscriptHandler.DEFAULT_SUBSCRIPT_SHIFT;
        Node subscriptshiftNode = node.getAttributes().getNamedItem("subscriptshift");
        if (subscriptshiftNode != null) {
            try {
                subscriptShift = Double.parseDouble(subscriptshiftNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please only specify a number without unit in the subscriptshift attribute of a <msubsup> node");
            }
        }

        // Parse superscriptshift attribute
        double superscriptShift = SuperscriptHandler.DEFAULT_SUPERSCRIPT_SHIFT;
        Node superscriptshiftNode = node.getAttributes().getNamedItem("superscriptshift");
        if (superscriptshiftNode != null) {
            try {
                superscriptShift = Double.parseDouble(superscriptshiftNode.getTextContent());
            } catch (NumberFormatException e) {
                throw new ParseException("Please only specify a number without unit in the superscriptshift attribute of a <msubsup> node");
            }
        }

        SubsuperscriptNode subsuperscriptNode = new SubsuperscriptNode(subscriptShift, superscriptShift);

        List<Node> children = new ArrayList<>();
        int len = node.getChildNodes().getLength();
        for (int i = 0; i < len; i++) {
            Node child = node.getChildNodes().item(i);

            if (child.getNodeType() == Node.ELEMENT_NODE) {
                children.add(child);
            }
        }

        // We expect exactly three child elements here!
        if (!node.hasChildNodes() || children.size() != 3) {
            throw new ParseException("A <msubsup> node is expected to have exactly 3 child nodes: <msubsup> base subscript superscript </msubsup>");
        }

        // Parse children
        for (Node child : children) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                MathMLNode childNode = MathMLParseContext.getParseHandler(child.getNodeName()).orElseThrow(() -> new ParseException(String.format(
                        "Could not find handler understanding how to deal with the '<%s>' MathML node",
                        child.getNodeName()
                ))).parse(child, ctx);

                subsuperscriptNode.addChild(childNode);
            }
        }

        return subsuperscriptNode;
    }

}
