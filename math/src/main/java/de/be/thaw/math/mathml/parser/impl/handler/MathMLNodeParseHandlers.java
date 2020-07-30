package de.be.thaw.math.mathml.parser.impl.handler;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.handler.impl.FractionHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.IdentifierHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.MathHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.NumericHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.OperatorHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.OverHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.PaddedHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.RootHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.RowHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.SpaceHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.SqrtHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.SubscriptHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.SubsupscriptHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.SuperscriptHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.TextHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.UnderHandler;
import de.be.thaw.math.mathml.parser.impl.handler.impl.UnderOverHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * Collection of available MathML node parse handlers.
 */
public class MathMLNodeParseHandlers {

    /**
     * Mapping of parse handlers.
     */
    private static final Map<String, MathMLNodeParseHandler> HANDLER_MAP = new HashMap<>();

    static {
        registerParseHandler(new MathHandler());
        registerParseHandler(new OperatorHandler());
        registerParseHandler(new IdentifierHandler());
        registerParseHandler(new NumericHandler());
        registerParseHandler(new FractionHandler());
        registerParseHandler(new RowHandler());
        registerParseHandler(new TextHandler());
        registerParseHandler(new SuperscriptHandler());
        registerParseHandler(new SubscriptHandler());
        registerParseHandler(new SubsupscriptHandler());
        registerParseHandler(new RootHandler());
        registerParseHandler(new SqrtHandler());
        registerParseHandler(new OverHandler());
        registerParseHandler(new UnderHandler());
        registerParseHandler(new UnderOverHandler());
        registerParseHandler(new SpaceHandler());
        registerParseHandler(new PaddedHandler());
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
     * @throws ParseException in case the node name is not parsable
     */
    public static MathMLNodeParseHandler getParseHandler(String nodeName) throws ParseException {
        return Optional.ofNullable(HANDLER_MAP.get(nodeName)).orElseThrow(() -> new ParseException(String.format(
                "Could not find handler understanding how to deal with the '<%s>' MathML node",
                nodeName
        )));
    }

}
