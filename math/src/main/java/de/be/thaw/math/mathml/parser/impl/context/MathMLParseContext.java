package de.be.thaw.math.mathml.parser.impl.context;

import de.be.thaw.math.mathml.parser.impl.handler.MathMLNodeParseHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.IdentifierHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.MathHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.NumericHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.OperatorHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Context used during MathML parsing.
 */
public class MathMLParseContext {

    /**
     * Mapping of parse handlers.
     */
    private final static Map<String, MathMLNodeParseHandler> HANDLER_MAP = new HashMap<>();

    static {
        registerParseHandler(new MathHandler());
        registerParseHandler(new OperatorHandler());
        registerParseHandler(new IdentifierHandler());
        registerParseHandler(new NumericHandler());
    }

    /**
     * Register the passed parse handler.
     *
     * @param handler to register
     */
    private static void registerParseHandler(MathMLNodeParseHandler handler) {
        HANDLER_MAP.put(handler.getNodeName(), handler);
    }

    /**
     * Get a parse handler for the passed node name (if there is any).
     *
     * @param nodeName to get parse handler for
     * @return the parse handler or an empty optional
     */
    public static Optional<MathMLNodeParseHandler> getParseHandler(String nodeName) {
        return Optional.ofNullable(HANDLER_MAP.get(nodeName));
    }

}
