package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.OverNode;
import de.be.thaw.util.HorizontalAlignment;
import org.w3c.dom.Node;

import java.util.ArrayList;
import java.util.List;

/**
 * Handler dealing with the over node.
 */
public class OverHandler extends AbstractMathMLNodeParseHandler {

    public OverHandler() {
        super("mover");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        // Parse align attribute
        HorizontalAlignment alignment = HorizontalAlignment.CENTER;
        Node alignNode = node.getAttributes().getNamedItem("align");
        if (alignNode != null) {
            try {
                alignment = HorizontalAlignment.valueOf(alignNode.getTextContent().toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                throw new ParseException(String.format(
                        "The value '%s' of the attribute 'denomalign' does not match any of the three allowed value 'center', 'left' or 'right'",
                        alignNode.getTextContent()
                ));
            }
        }

        OverNode overNode = new OverNode(alignment);

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
            throw new ParseException("A <mover> node is expected to have exactly 2 child nodes. <mover> base overscript </mover>");
        }

        // Parse children
        for (Node child : children) {
            if (child.getNodeType() == Node.ELEMENT_NODE) {
                MathMLNode childNode = MathMLParseContext.getParseHandler(child.getNodeName()).orElseThrow(() -> new ParseException(String.format(
                        "Could not find handler understanding how to deal with the '<%s>' MathML node",
                        child.getNodeName()
                ))).parse(child, ctx);

                overNode.addChild(childNode);
            }
        }

        return overNode;
    }

}
