package de.be.thaw.math.mathml.typeset;

import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;

import java.util.List;

/**
 * Typesetter for a MathML tree.
 */
public interface MathMLTypesetter {

    /**
     * Typeset the passed MathML tree.
     *
     * @param tree to typeset
     * @return the typeset elements
     * @throws TypesetException in case the MathML tree could not be typeset properly
     */
    List<MathElement> typeset(MathMLTree tree) throws TypesetException;

}
