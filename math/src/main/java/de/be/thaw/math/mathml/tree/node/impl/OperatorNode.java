package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;

/**
 * Operator <mo> node.
 */
public class OperatorNode extends MathMLNode {

    /**
     * The operator in string form.
     */
    private final String operator;

    public OperatorNode(String operator) {
        super("mo");

        this.operator = operator;
    }

    /**
     * Get the operator.
     *
     * @return operator
     */
    public String getOperator() {
        return operator;
    }

}
