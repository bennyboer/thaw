package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SubscriptNode;
import org.jsoup.nodes.Element;

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
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse attributes
        double subscriptShift = getDoubleAttribute(element, "subscriptshift", DEFAULT_SUBSCRIPT_SHIFT);

        // Create node
        SubscriptNode subscriptNode = new SubscriptNode(subscriptShift);

        // We expect exactly two child elements here!
        if (element.childrenSize() != 2) {
            throw new ParseException("A <msub> node is expected to have exactly 2 child elements. <msub> base subscript </msub>");
        }

        // Parse children
        for (Element child : element.children()) {
            subscriptNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return subscriptNode;
    }

}
