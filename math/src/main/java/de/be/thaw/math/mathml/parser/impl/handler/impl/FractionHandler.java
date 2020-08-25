package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.FractionNode;
import de.be.thaw.util.HorizontalAlignment;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with the fraction node.
 */
public class FractionHandler extends AbstractMathMLNodeParseHandler {

    public FractionHandler() {
        super("mfrac");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse attributes
        boolean bevelled = getBooleanAttribute(element, "bevelled", false);
        double lineThickness = getDoubleAttribute(element, "linethickness", ctx.getConfig().getDefaultLineThickness());
        HorizontalAlignment numeratorAlignment = getAlignmentAttribute(element, "numalign", HorizontalAlignment.CENTER);
        HorizontalAlignment denominatorAlignment = getAlignmentAttribute(element, "denomalign", HorizontalAlignment.CENTER);

        // Create node
        MathMLNode fractionNode = new FractionNode(bevelled, numeratorAlignment, denominatorAlignment, lineThickness);

        // We expect exactly two child elements here!
        if (element.childrenSize() != 2) {
            throw new ParseException("A <mfrac> node is expected to have exactly 2 child nodes");
        }

        // Parse children
        for (Element child : element.children()) {
            MathMLNode childNode = MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx);

            fractionNode.addChild(childNode);
        }

        return fractionNode;
    }

}
