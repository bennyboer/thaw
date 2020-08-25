package de.be.thaw.math.mathml.typeset.impl;

import de.be.thaw.math.mathml.typeset.config.MathTypesetConfig;
import de.be.thaw.util.Size;

/**
 * Context used during typesetting.
 */
public class MathTypesetContext {

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

    /**
     * Preferred size of the currently processing element.
     */
    private Size preferredSize = new Size(0, 0);

    public MathTypesetContext(MathTypesetConfig config) {
        this.config = config;
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
        return Math.max(getConfig().getFontSize() - getConfig().getFontSize() * 0.1 * getLevel(), 4);
    }

    public Size getPreferredSize() {
        return preferredSize;
    }

    public void setPreferredSize(Size preferredSize) {
        this.preferredSize = preferredSize;
    }

}
