package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.UnderNode;
import de.be.thaw.util.HorizontalAlignment;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with the under node.
 */
public class UnderHandler extends AbstractMathMLNodeParseHandler {

    public UnderHandler() {
        super("munder");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse attributes
        HorizontalAlignment alignment = getAlignmentAttribute(element, "align", HorizontalAlignment.CENTER);

        // Create node
        UnderNode underNode = new UnderNode(alignment);

        // We expect exactly two child elements here!
        if (element.childrenSize() != 2) {
            throw new ParseException("A <munder> node is expected to have exactly 2 child elements. <munder> base underscript </munder>");
        }

        // Parse children
        for (Element child : element.children()) {
            underNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return underNode;
    }

}
