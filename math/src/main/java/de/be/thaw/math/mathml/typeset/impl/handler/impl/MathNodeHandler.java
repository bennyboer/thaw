package de.be.thaw.math.mathml.typeset.impl.handler.impl;

/**
 * Handler dealing with the root math node.
 */
public class MathNodeHandler extends RowNodeHandler {

    @Override
    public String supportedNodeName() {
        return "math";
    }

}
