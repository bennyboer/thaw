package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.IdentifierNode;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;

/**
 * Handler dealing with identifier nodes.
 */
public class IdentifierNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mi";
    }

    @Override
    public void handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        IdentifierNode mi = (IdentifierNode) node;

        String text = mi.getText();

        // TODO Deal with different font variants


    }

}
