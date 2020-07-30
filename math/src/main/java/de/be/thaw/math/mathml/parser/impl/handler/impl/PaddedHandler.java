package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.PaddedNode;
import org.jsoup.nodes.Element;

/**
 * Handler for <mpadded> MathML node.
 */
public class PaddedHandler extends TokenNodeHandler {

    public PaddedHandler() {
        super("mpadded");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse attributes
        String width = getStringAttribute(element, "width", null);
        String depth = getStringAttribute(element, "depth", null);
        String height = getStringAttribute(element, "height", null);
        String lspace = getStringAttribute(element, "lspace", null);
        String voffset = getStringAttribute(element, "voffset", null);

        PaddedNode paddedNode = new PaddedNode(width, height, depth, lspace, voffset);

        // We expect exactly one child elements here!
        if (element.childrenSize() != 1) {
            throw new ParseException("A <mpadded> node is expected to have exactly 1 child element: <mpadded> basis </mpadded>");
        }

        // Parse child elements
        for (Element child : element.children()) {
            paddedNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return paddedNode;
    }

}
