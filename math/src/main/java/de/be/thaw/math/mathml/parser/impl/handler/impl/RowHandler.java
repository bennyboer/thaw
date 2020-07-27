package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.RowNode;
import org.w3c.dom.Node;

/**
 * Handler dealing with the row element of a MathML tree.
 */
public class RowHandler extends AbstractMathMLNodeParseHandler {

    public RowHandler() {
        super("mrow");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        MathMLNode rowNode = new RowNode();

        // TODO We may allow special attributes here (mathbackground, mathcolor, overflow, ...)

        // Parse child elements
        if (node.hasChildNodes()) {
            int len = node.getChildNodes().getLength();
            for (int i = 0; i < len; i++) {
                Node child = node.getChildNodes().item(i);

                if (child.getNodeType() == Node.ELEMENT_NODE) {
                    MathMLNode childNode = MathMLParseContext.getParseHandler(child.getNodeName()).orElseThrow(() -> new ParseException(String.format(
                            "Could not find handler understanding how to deal with the '<%s>' MathML node",
                            child.getNodeName()
                    ))).parse(child, ctx);

                    rowNode.addChild(childNode);
                }
            }
        }

        return rowNode;
    }

}
