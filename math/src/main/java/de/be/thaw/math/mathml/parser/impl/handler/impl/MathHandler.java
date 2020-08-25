package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.MathNode;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with the root element of a MathML tree.
 */
public class MathHandler extends AbstractMathMLNodeParseHandler {

    public MathHandler() {
        super("math");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        MathMLNode mathNode = new MathNode();

        /*
        "The math element can contain an arbitrary number of child elements.
        They render by default as if they were contained in an mrow element."
        (Source: https://www.w3.org/TR/MathML3/chapter2.html#interf.toplevel)

        Thus we do not restrict the allowed child elements for the root node.
         */

        // Parse child elements
        for (Element child : element.children()) {
            mathNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return mathNode;
    }

}
