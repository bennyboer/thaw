package de.be.thaw.math.mathml.typeset.impl;

import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.typeset.MathExpression;
import de.be.thaw.math.mathml.typeset.MathMLTypesetter;
import de.be.thaw.math.mathml.typeset.config.MathTypesetConfig;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.util.Size;

/**
 * The default MathML typesetter.
 */
public class DefaultMathMLTypesetter implements MathMLTypesetter {

    @Override
    public MathExpression typeset(MathMLTree tree, MathTypesetConfig config) throws TypesetException {
        MathTypesetContext ctx = new MathTypesetContext(config);

        MathTypesetContext.getHandler(tree.getRoot().getName())
                .orElseThrow(() -> new TypesetException("Could not typeset MathML tree without <math> root"))
                .handle(tree.getRoot(), ctx);

        // Determine size of the math expression
        double minX = 0;
        double maxX = 0;
        double minY = 0;
        double maxY = 0;
        for (MathElement element : ctx.getElements()) {
            if (element.getPosition().getY() < minY) {
                minY = element.getPosition().getY();
            }
            if (element.getPosition().getX() < minX) {
                minX = element.getPosition().getX();
            }
            if (element.getPosition().getY() + element.getSize().getHeight() > maxY) {
                maxY = element.getPosition().getY() + element.getSize().getHeight();
            }
            if (element.getPosition().getX() + element.getSize().getWidth() > maxX) {
                maxX = element.getPosition().getX() + element.getSize().getWidth();
            }
        }

        return new MathExpression(ctx.getElements(), new Size(maxX - minX, maxY - minY));
    }

}
