package de.be.thaw.math.mathml.tree.util.operator;

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
     * Whether the operator should be stretchy.
     */
    private final boolean stretchy;

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

    public OperatorDictionaryEntry(
            char[] operator,
            OperatorForm form,
            String description,
            int priority,
            int lspace,
            int rspace,
            boolean fence,
            boolean stretchy,
            boolean symmetric,
            boolean separator,
            boolean accent,
            boolean movablelimits,
            boolean largeop
    ) {
        this.operator = operator;
        this.form = form;
        this.description = description;
        this.priority = priority;
        this.lspace = lspace;
        this.rspace = rspace;
        this.fence = fence;
        this.stretchy = stretchy;
        this.symmetric = symmetric;
        this.separator = separator;
        this.accent = accent;
        this.movablelimits = movablelimits;
        this.largeop = largeop;
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

    public boolean isStretchy() {
        return stretchy;
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

    public boolean isLargeop() {
        return largeop;
    }

}
