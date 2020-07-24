package de.be.thaw.math.mathml.typeset.impl.handler;

/**
 * Handler dealing with the root math node.
 */
public class MathHandler extends RowHandler {

    @Override
    public String supportedNodeName() {
        return "math";
    }

}
