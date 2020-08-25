package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SuperscriptNode;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with the superscript node.
 */
public class SuperscriptHandler extends AbstractMathMLNodeParseHandler {

    /**
     * The default superscript shift.
     * 0.5 means 50 % above the baseline.
     */
    static final double DEFAULT_SUPERSCRIPT_SHIFT = 0.6;

    public SuperscriptHandler() {
        super("msup");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse superscriptshift attribute
        double superscriptShift = getDoubleAttribute(element, "superscriptshift", DEFAULT_SUPERSCRIPT_SHIFT);

        // Create node
        SuperscriptNode superscriptNode = new SuperscriptNode(superscriptShift);

        // We expect exactly two child elements here!
        if (element.childrenSize() != 2) {
            throw new ParseException("A <msup> node is expected to have exactly 2 child elements. <msup> base superscript </msup>");
        }

        // Parse children
        for (Element child : element.children()) {
            superscriptNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return superscriptNode;
    }

}
