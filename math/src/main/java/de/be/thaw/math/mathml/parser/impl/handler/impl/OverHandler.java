package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.OverNode;
import de.be.thaw.util.HorizontalAlignment;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with the over node.
 */
public class OverHandler extends AbstractMathMLNodeParseHandler {

    public OverHandler() {
        super("mover");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse attributes
        HorizontalAlignment alignment = getAlignmentAttribute(element, "align", HorizontalAlignment.CENTER);

        // Create node
        OverNode overNode = new OverNode(alignment);

        // We expect exactly two child elements here!
        if (element.childrenSize() != 2) {
            throw new ParseException("A <mover> node is expected to have exactly 2 child elements. <mover> base overscript </mover>");
        }

        // Parse children
        for (Element child : element.children()) {
            overNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return overNode;
    }

}
