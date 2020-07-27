package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.MathNode;
import org.w3c.dom.Node;

/**
 * Handler dealing with the root element of a MathML tree.
 */
public class MathHandler extends AbstractMathMLNodeParseHandler {

    public MathHandler() {
        super("math");
    }

    @Override
    public MathMLNode parse(Node node, MathMLParseContext ctx) throws ParseException {
        MathMLNode mathNode = new MathNode();

        /*
        "The math element can contain an arbitrary number of child elements.
        They render by default as if they were contained in an mrow element."
        (Source: https://www.w3.org/TR/MathML3/chapter2.html#interf.toplevel)

        Thus we do not restrict the allowed child elements for the root node.
         */

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

                    mathNode.addChild(childNode);
                }
            }
        }

        return mathNode;
    }

}
