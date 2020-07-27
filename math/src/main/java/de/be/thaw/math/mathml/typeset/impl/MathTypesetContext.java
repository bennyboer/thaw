package de.be.thaw.math.mathml.typeset.impl;

import de.be.thaw.math.mathml.typeset.config.MathTypesetConfig;
import de.be.thaw.math.mathml.typeset.impl.handler.FractionHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.IdentifierNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.NumericNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.OperatorNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.RowHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.TextNodeHandler;

import java.util.HashMap;
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
        registerHandler(new MathHandler());
        registerHandler(new RowHandler());
        registerHandler(new IdentifierNodeHandler());
        registerHandler(new OperatorNodeHandler());
        registerHandler(new NumericNodeHandler());
        registerHandler(new FractionHandler());
        registerHandler(new TextNodeHandler());
    }

    /**
     * Config to use during typesetting.
     */
    private final MathTypesetConfig config;

    /**
     * The current Y-coordinate.
     */
    private double currentY = 0;

    /**
     * The current X-coordinate.
     */
    private double currentX = 0;

    /**
     * Nesting level in the expression.
     */
    private int level = 0;

    public MathTypesetContext(MathTypesetConfig config) {
        this.config = config;
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
     * Get the configuration to use during typesetting.
     *
     * @return config
     */
    public MathTypesetConfig getConfig() {
        return config;
    }

    public double getCurrentY() {
        return currentY;
    }

    public void setCurrentY(double currentY) {
        this.currentY = currentY;
    }

    public double getCurrentX() {
        return currentX;
    }

    public void setCurrentX(double currentX) {
        this.currentX = currentX;
    }

    public int getLevel() {
        return level;
    }

    public void setLevel(int level) {
        this.level = level;
    }

    public double getLevelAdjustedFontSize() {
        return Math.max(getConfig().getFontSize() - getLevel(), 8.0);
    }

}
