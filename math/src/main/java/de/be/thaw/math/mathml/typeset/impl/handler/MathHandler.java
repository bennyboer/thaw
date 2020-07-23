package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.MathNode;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;

/**
 * Handler dealing with the root math node.
 */
public class MathHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "math";
    }

    @Override
    public void handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        MathNode math = (MathNode) node;

        for (MathMLNode child : math.getChildren()) {
            MathTypesetContext.getHandler(child.getName()).orElseThrow(() -> new TypesetException(String.format(
                    "Could not find a handler for the MathML node '%s'",
                    child.getName()
            ))).handle(child, ctx);
        }
    }

}
