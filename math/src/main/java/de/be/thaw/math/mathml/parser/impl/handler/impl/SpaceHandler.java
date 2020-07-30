package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.SpaceNode;
import org.jsoup.nodes.Element;

/**
 * Handler for <mspace> MathML node.
 */
public class SpaceHandler extends TokenNodeHandler {

    public SpaceHandler() {
        super("mspace");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        // Parse attributes
        double width = getDoubleAttribute(element, "width", 0.0);
        double depth = getDoubleAttribute(element, "depth", 0.0);
        double height = getDoubleAttribute(element, "height", 0.0);

        return new SpaceNode(width, height, depth);
    }

}
