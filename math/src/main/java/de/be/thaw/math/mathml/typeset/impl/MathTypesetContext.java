package de.be.thaw.math.mathml.typeset.impl;

import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.impl.handler.IdentifierNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Context used during typesetting.
 */
public class MathTypesetContext {

    /**
     * Mapping of handlers by their supported node names.
     */
    private static final Map<String, MathMLNodeHandler> HANDLER_MAP = new HashMap<>();

    static {
        registerHandler(new IdentifierNodeHandler());
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
     */
    public static Optional<MathMLNodeHandler> getHandler(String nodeName) {
        return Optional.of(HANDLER_MAP.get(nodeName));
    }

    /**
     * The typeset elements.
     */
    private final List<MathElement> elements = new ArrayList<>();

    /**
     * Get the typeset elements.
     *
     * @return typeset elements
     */
    public List<MathElement> getElements() {
        return elements;
    }

    /**
     * Push an element to the typeset elements.
     *
     * @param element to push
     */
    public void pushElement(MathElement element) {
        elements.add(element);
    }

}
