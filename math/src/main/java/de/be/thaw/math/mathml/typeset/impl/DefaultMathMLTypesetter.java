package de.be.thaw.math.mathml.typeset.impl;

import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.typeset.MathExpression;
import de.be.thaw.math.mathml.typeset.MathMLTypesetter;
import de.be.thaw.math.mathml.typeset.config.MathTypesetConfig;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;

/**
 * The default MathML typesetter.
 */
public class DefaultMathMLTypesetter implements MathMLTypesetter {

    @Override
    public MathExpression typeset(MathMLTree tree, MathTypesetConfig config) throws TypesetException {
        MathTypesetContext ctx = new MathTypesetContext(config);

        MathElement root = MathNodeHandlers.getHandler(tree.getRoot().getName())
                .handle(tree.getRoot(), ctx);

        return new MathExpression(root);
    }

}
