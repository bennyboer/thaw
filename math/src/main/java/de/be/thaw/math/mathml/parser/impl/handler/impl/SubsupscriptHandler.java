package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SubsuperscriptNode;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with the subsupscript node.
 */
public class SubsupscriptHandler extends AbstractMathMLNodeParseHandler {

    public SubsupscriptHandler() {
        super("msubsup");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse attributes
        double subscriptShift = getDoubleAttribute(element, "subscriptshift", SubscriptHandler.DEFAULT_SUBSCRIPT_SHIFT);
        double superscriptShift = getDoubleAttribute(element, "superscriptshift", SuperscriptHandler.DEFAULT_SUPERSCRIPT_SHIFT);

        // Create node
        SubsuperscriptNode subsuperscriptNode = new SubsuperscriptNode(subscriptShift, superscriptShift);

        // We expect exactly three child elements here!
        if (element.childrenSize() != 3) {
            throw new ParseException("A <msubsup> node is expected to have exactly 3 child elements: <msubsup> base subscript superscript </msubsup>");
        }

        // Parse children
        for (Element child : element.children()) {
            subsuperscriptNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return subsuperscriptNode;
    }

}
