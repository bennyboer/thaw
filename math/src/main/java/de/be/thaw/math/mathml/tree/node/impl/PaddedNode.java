package de.be.thaw.math.mathml.tree.node.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import org.jetbrains.annotations.Nullable;

/**
 * Padded <mpadded> node.
 */
public class PaddedNode extends MathMLNode {

    /**
     * The width adjustment.
     */
    @Nullable
    private final String widthAdjustment;

    /**
     * The height adjustment (baseline).
     */
    @Nullable
    private final String heightAdjustment;

    /**
     * Depth (height under the baseline) adjustment.
     */
    @Nullable
    private final String depthAdjustment;

    /**
     * X-position adjustment.
     */
    @Nullable
    private final String xAdjustment;

    /**
     * Y-position adjustment.
     */
    @Nullable
    private final String yAdjustment;

    public PaddedNode(@Nullable String widthAdjustment, @Nullable String heightAdjustment, @Nullable String depthAdjustment, @Nullable String xAdjustment, @Nullable String yAdjustment) {
        super("mpadded");

        this.widthAdjustment = widthAdjustment;
        this.heightAdjustment = heightAdjustment;
        this.depthAdjustment = depthAdjustment;
        this.xAdjustment = xAdjustment;
        this.yAdjustment = yAdjustment;
    }

    public String getWidthAdjustment() {
        return widthAdjustment;
    }

    public String getHeightAdjustment() {
        return heightAdjustment;
    }

    public String getDepthAdjustment() {
        return depthAdjustment;
    }

    public String getXAdjustment() {
        return xAdjustment;
    }

    public String getYAdjustment() {
        return yAdjustment;
    }

}
