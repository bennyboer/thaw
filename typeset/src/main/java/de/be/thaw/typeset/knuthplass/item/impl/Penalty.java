package de.be.thaw.typeset.knuthplass.item.impl;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.item.AbstractItem;
import de.be.thaw.typeset.knuthplass.item.ItemType;
import org.jetbrains.annotations.Nullable;

/**
 * A penalty specified a potential line breaking possibility
 * where its value means the aesthetic cost (low -> good, high -> bad).
 */
public class Penalty extends AbstractItem {

    /**
     * The maximum penalty.
     * All penalties greater or equal to this are treated as impossible line breaks.
     */
    public static final double MAX_PENALTY = 1000.0;

    /**
     * The minimum penalty.
     * All penalties lower or equal to this are treated as mandatory line breaks.
     */
    public static final double MIN_PENALTY = -1000.0;

    /**
     * Penalty to line break at this point.
     */
    private double penalty;

    /**
     * Width added to the line when breaking at this point.
     * For example the length of a hyphen when breaking here.
     */
    private final double width;

    /**
     * Whether the penalty is flagged.
     * The Knuth-Plass algorithm is trying to avoid two consecutive line breaks at
     * flagged penalties (for example two hyphenations in a row).
     */
    private final boolean flagged;

    /**
     * The original node this penalty (when a hyphen) belongs to.
     */
    private final DocumentNode node;

    /**
     * String metrics of the penalty replacement string (if any).
     */
    @Nullable
    private FontDetailsSupplier.StringMetrics metrics;

    /**
     * Replacement string of the penalty (if any).
     */
    @Nullable
    private String replacementString;

    public Penalty(double penalty, double width, boolean flagged) {
        this(penalty, width, flagged, null);
    }

    public Penalty(double penalty, double width, boolean flagged, DocumentNode node) {
        this.penalty = penalty;
        this.width = width;
        this.flagged = flagged;
        this.node = node;
    }

    @Override
    public ItemType getType() {
        return ItemType.PENALTY;
    }

    @Override
    public double getWidth() {
        return width;
    }

    @Override
    public double getPenalty() {
        return penalty;
    }

    @Override
    public boolean isFlagged() {
        return flagged;
    }

    /**
     * Set the penalty.
     *
     * @param penalty to set
     */
    public void setPenalty(double penalty) {
        this.penalty = penalty;
    }

    /**
     * Whether the line break at this point is mandatory.
     *
     * @return mandatory line break
     */
    public boolean isMandatoryLineBreak() {
        return penalty <= MIN_PENALTY;
    }

    /**
     * Whether the line break at this point is impossible.
     *
     * @return impossible line break
     */
    public boolean isImpossibleLineBreak() {
        return penalty >= MAX_PENALTY;
    }

    /**
     * When this penalty is a hyphen, this is non-null and represents the original node the
     * hyphen belongs to.
     *
     * @return node
     */
    public DocumentNode getNode() {
        return node;
    }

    /**
     * Get the metrics of the replacement string (if any).
     *
     * @return metrics
     */
    @Nullable
    public FontDetailsSupplier.StringMetrics getMetrics() {
        return metrics;
    }

    /**
     * Set the metrics of the replacement string (if any).
     *
     * @param metrics to set
     */
    public void setMetrics(@Nullable FontDetailsSupplier.StringMetrics metrics) {
        this.metrics = metrics;
    }

    /**
     * Get the replacement string of the penalty (if any).
     *
     * @return replacement string
     */
    @Nullable
    public String getReplacementString() {
        return replacementString;
    }

    /**
     * Set the replacement string of the penalty.
     *
     * @param replacementString to set
     */
    public void setReplacementString(@Nullable String replacementString) {
        this.replacementString = replacementString;
    }

}
