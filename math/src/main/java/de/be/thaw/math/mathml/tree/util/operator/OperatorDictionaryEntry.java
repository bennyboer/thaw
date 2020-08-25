package de.be.thaw.math.mathml.tree.util.operator;

import org.jetbrains.annotations.Nullable;

/**
 * Entry of the operator dictionary.
 * It gives recommended default attribute values for a
 * operator character and form (prefix, infix or postfix).
 * See https://www.w3.org/TR/MathML3/appendixc.html.
 */
public class OperatorDictionaryEntry {

    /**
     * The operators characters.
     */
    private final char[] operator;

    /**
     * Form of the operator.
     */
    private final OperatorForm form;

    /**
     * Description of the operator.
     */
    private final String description;

    /**
     * Priority of the operator.
     */
    private final int priority;

    /**
     * Left space width.
     */
    private final int lspace;

    /**
     * Right space width.
     */
    private final int rspace;

    /**
     * Fence property.
     */
    private final boolean fence;

    /**
     * Whether the operator should be stretchy vertically.
     */
    private final boolean verticalStretchy;

    /**
     * Whether the operator should be stretchy horizontally.
     */
    private final boolean horizontalStretchy;

    /**
     * Whether the operator should be symmetric.
     */
    private final boolean symmetric;

    /**
     * Whether the operator is a separator.
     */
    private final boolean separator;

    /**
     * Whether the operator should be displayed as accent.
     */
    private final boolean accent;

    /**
     * Whether underscripts or overscripts are allowed to be displayed
     * at the super- or subscript positions.
     */
    private final boolean movablelimits;

    /**
     * Whether the operator should be displayed larger than usually.
     */
    private final boolean largeop;

    /**
     * Start of the large operator character (if any).
     */
    @Nullable
    private Character largeOpStart;

    /**
     * Middle of the large operator character (if any).
     */
    @Nullable
    private Character largeOpMiddle;

    /**
     * End of the large operator character (if any).
     */
    @Nullable
    private Character largeOpEnd;

    /**
     * Extension of the large operator character (if any).
     */
    @Nullable
    private Character largeOpExtension;

    public OperatorDictionaryEntry(
            char[] operator,
            OperatorForm form,
            String description,
            int priority,
            int lspace,
            int rspace,
            boolean fence,
            boolean verticalStretchy,
            boolean horizontalStretchy,
            boolean symmetric,
            boolean separator,
            boolean accent,
            boolean movablelimits,
            boolean largeop
    ) {
        this(operator, form, description, priority, lspace, rspace, fence, verticalStretchy, horizontalStretchy, symmetric, separator, accent, movablelimits, largeop, null, null, null, null);
    }

    public OperatorDictionaryEntry(
            char[] operator,
            OperatorForm form,
            String description,
            int priority,
            int lspace,
            int rspace,
            boolean fence,
            boolean verticalStretchy,
            boolean horizontalStretchy,
            boolean symmetric,
            boolean separator,
            boolean accent,
            boolean movablelimits,
            boolean largeop,
            @Nullable Character largeOpStart,
            @Nullable Character largeOpMiddle,
            @Nullable Character largeOpEnd,
            @Nullable Character largeOpExtension
    ) {
        this.operator = operator;
        this.form = form;
        this.description = description;
        this.priority = priority;
        this.lspace = lspace;
        this.rspace = rspace;
        this.fence = fence;
        this.verticalStretchy = verticalStretchy;
        this.horizontalStretchy = horizontalStretchy;
        this.symmetric = symmetric;
        this.separator = separator;
        this.accent = accent;
        this.movablelimits = movablelimits;
        this.largeop = largeop;
        this.largeOpStart = largeOpStart;
        this.largeOpMiddle = largeOpMiddle;
        this.largeOpEnd = largeOpEnd;
        this.largeOpExtension = largeOpExtension;
    }

    public char[] getOperator() {
        return operator;
    }

    public OperatorForm getForm() {
        return form;
    }

    public String getDescription() {
        return description;
    }

    public int getPriority() {
        return priority;
    }

    public int getLspace() {
        return lspace;
    }

    public int getRspace() {
        return rspace;
    }

    public boolean isFence() {
        return fence;
    }

    public boolean isVerticalStretchy() {
        return verticalStretchy;
    }

    public boolean isHorizontalStretchy() {
        return horizontalStretchy;
    }

    public boolean isSymmetric() {
        return symmetric;
    }

    public boolean isSeparator() {
        return separator;
    }

    public boolean isAccent() {
        return accent;
    }

    public boolean isMovablelimits() {
        return movablelimits;
    }

    public boolean isLargeOp() {
        return largeop;
    }

    @Nullable
    public Character getLargeOpStart() {
        return largeOpStart;
    }

    @Nullable
    public Character getLargeOpMiddle() {
        return largeOpMiddle;
    }

    @Nullable
    public Character getLargeOpEnd() {
        return largeOpEnd;
    }

    @Nullable
    public Character getLargeOpExtension() {
        return largeOpExtension;
    }

}
