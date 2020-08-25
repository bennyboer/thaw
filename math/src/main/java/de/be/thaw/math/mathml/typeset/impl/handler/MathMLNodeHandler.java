package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import org.jetbrains.annotations.Nullable;

/**
 * Handler dealing with a MathML node for typesetting.
 */
public interface MathMLNodeHandler {

    /**
     * The node name this handler supports to deal with.
     *
     * @return node name
     */
    String supportedNodeName();

    /**
     * Handle the passed node.
     *
     * @param node   to typeset
     * @param ctx    used during typesetting
     * @return the produced math element
     * @throws TypesetException in case the passed node could not be typeset properly
     */
    MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException;

}
