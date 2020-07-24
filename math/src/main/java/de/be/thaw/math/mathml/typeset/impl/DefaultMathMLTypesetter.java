package de.be.thaw.math.mathml.typeset.impl;

import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.typeset.MathExpression;
import de.be.thaw.math.mathml.typeset.MathMLTypesetter;
import de.be.thaw.math.mathml.typeset.config.MathTypesetConfig;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;

/**
 * The default MathML typesetter.
 */
public class DefaultMathMLTypesetter implements MathMLTypesetter {

    @Override
    public MathExpression typeset(MathMLTree tree, MathTypesetConfig config) throws TypesetException {
        MathTypesetContext ctx = new MathTypesetContext(config);

        MathElement root = MathTypesetContext.getHandler(tree.getRoot().getName())
                .orElseThrow(() -> new TypesetException("Could not typeset MathML tree without <math> root"))
                .handle(tree.getRoot(), ctx);

        return new MathExpression(root);
    }

}
