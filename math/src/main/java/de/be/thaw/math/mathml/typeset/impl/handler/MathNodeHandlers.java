package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.FractionNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.IdentifierNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.MathNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.NumericNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.OperatorNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.OverNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.RootNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.RowNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.SpaceNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.SqrtNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.SubscriptNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.SubsuperscriptNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.SuperscriptNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.TextNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.UnderNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.impl.UnderOverNodeHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Collection of math node handlers.
 */
public class MathNodeHandlers {

    /**
     * Mapping of handlers by their supported node names.
     */
    private static final Map<String, MathMLNodeHandler> HANDLER_MAP = new HashMap<>();

    static {
        registerHandler(new MathNodeHandler());
        registerHandler(new RowNodeHandler());
        registerHandler(new IdentifierNodeHandler());
        registerHandler(new OperatorNodeHandler());
        registerHandler(new NumericNodeHandler());
        registerHandler(new FractionNodeHandler());
        registerHandler(new TextNodeHandler());
        registerHandler(new SuperscriptNodeHandler());
        registerHandler(new SubscriptNodeHandler());
        registerHandler(new SubsuperscriptNodeHandler());
        registerHandler(new RootNodeHandler());
        registerHandler(new SqrtNodeHandler());
        registerHandler(new OverNodeHandler());
        registerHandler(new UnderNodeHandler());
        registerHandler(new UnderOverNodeHandler());
        registerHandler(new SpaceNodeHandler());
    }

    /**
     * Register the passed handler.
     *
     * @param handler to register
     */
    private static void registerHandler(MathMLNodeHandler handler) {
        HANDLER_MAP.put(handler.supportedNodeName(), handler);
    }

    /**
     * Get a handler for the passed MathML node name.
     *
     * @param nodeName to get handler for
     * @return an optional handler
     * @throws TypesetException in case the handler could not be found
     */
    public static MathMLNodeHandler getHandler(String nodeName) throws TypesetException {
        return Optional.of(HANDLER_MAP.get(nodeName)).orElseThrow(() -> new TypesetException(String.format(
                "Could not find a handler for the MathML node with name '%s'",
                nodeName
        )));
    }

}
