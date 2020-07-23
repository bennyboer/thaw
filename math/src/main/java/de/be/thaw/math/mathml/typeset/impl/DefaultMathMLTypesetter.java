package de.be.thaw.math.mathml.typeset.impl;

import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.typeset.MathMLTypesetter;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;

import java.util.List;

/**
 * The default MathML typesetter.
 */
public class DefaultMathMLTypesetter implements MathMLTypesetter {

    public DefaultMathMLTypesetter() {
        // TODO Add input argument (math font, font size, ...)
    }

    @Override
    public List<MathElement> typeset(MathMLTree tree) throws TypesetException {
        MathTypesetContext ctx = new MathTypesetContext(); // TODO Set config to context (math font, font size, ...)

        MathTypesetContext.getHandler(tree.getRoot().getName())
                .orElseThrow(() -> new TypesetException("Could not typeset MathML tree without <math> root"))
                .handle(tree.getRoot(), ctx);

        return ctx.getElements();
    }

}
