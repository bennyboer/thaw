package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.parser.impl.handler.AbstractMathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandlers;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.RowNode;
import org.jsoup.nodes.Element;

/**
 * Handler dealing with the row element of a MathML tree.
 */
public class RowHandler extends AbstractMathMLNodeParseHandler {

    public RowHandler() {
        super("mrow");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        MathMLNode rowNode = new RowNode();

        // Parse child elements
        for (Element child : element.children()) {
            rowNode.addChild(MathMLNodeParseHandlers.getParseHandler(child.nodeName())
                    .parse(child, ctx));
        }

        return rowNode;
    }

}
