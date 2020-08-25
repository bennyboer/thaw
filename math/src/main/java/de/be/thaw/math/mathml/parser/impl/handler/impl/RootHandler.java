package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.RootNode;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with a root node.
 */
public class RootHandler extends AbstractMathMLNodeParseHandler {

    public RootHandler() {
        super("mroot");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        RootNode rootNode = new RootNode(ctx.getConfig().getDefaultLineThickness());

        // We expect exactly two child elements here!
        if (element.childrenSize() != 2) {
            throw new ParseException("A <mroot> node is expected to have exactly 2 child elements: <mroot> basis exponent </mroot>");
        }

        // Parse children
        for (Element child : element.children()) {
            rootNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return rootNode;
    }

}
