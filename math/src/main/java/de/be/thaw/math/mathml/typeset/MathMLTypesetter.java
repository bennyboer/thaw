package de.be.thaw.math.mathml.typeset;

import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.typeset.config.MathTypesetConfig;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;

/**
 * Typesetter for a MathML tree.
 */
public interface MathMLTypesetter {

    /**
     * Typeset the passed MathML tree.
     *
     * @param tree   to typeset
     * @param config to use during typesetting
     * @return the typeset elements
     * @throws TypesetException in case the MathML tree could not be typeset properly
     */
    MathExpression typeset(MathMLTree tree, MathTypesetConfig config) throws TypesetException;

}
