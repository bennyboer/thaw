package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SqrtNode;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with a sqrt node.
 */
public class SqrtHandler extends AbstractMathMLNodeParseHandler {

    public SqrtHandler() {
        super("msqrt");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        SqrtNode sqrtNode = new SqrtNode(ctx.getConfig().getDefaultLineThickness());

        // We expect exactly one child elements here!
        if (element.childrenSize() != 1) {
            throw new ParseException("A <msqrt> node is expected to have exactly 1 child element: <msqrt> basis </msqrt>");
        }

        // Parse children
        for (Element child : element.children()) {
            sqrtNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return sqrtNode;
    }

}
