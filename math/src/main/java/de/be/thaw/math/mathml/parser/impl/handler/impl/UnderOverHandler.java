package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.UnderOverNode;
import de.be.thaw.util.HorizontalAlignment;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with the under and over node.
 */
public class UnderOverHandler extends AbstractMathMLNodeParseHandler {

    public UnderOverHandler() {
        super("munderover");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse attributes
        HorizontalAlignment alignment = getAlignmentAttribute(element, "align", HorizontalAlignment.CENTER);

        // Create node
        UnderOverNode underOverNode = new UnderOverNode(alignment);

        // We expect exactly two child elements here!
        if (element.childrenSize() != 3) {
            throw new ParseException("A <munderover> node is expected to have exactly 3 child elements. <munderover> base underscript overscript </munderover>");
        }

        // Parse children
        for (Element child : element.children()) {
            underOverNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return underOverNode;
    }

}
