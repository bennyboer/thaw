package de.be.thaw.math.mathml.tree.util.operator;

import java.util.Arrays;

/**
 * Key of a operator dictionary entry.
 */
public class OperatorDictionaryEntryKey {

    /**
     * Form of the operator.
     */
    private final OperatorForm form;

    /**
     * Characters of the operator.
     */
    private final char[] operator;

    public OperatorDictionaryEntryKey(OperatorForm form, char[] operator) {
        this.form = form;
        this.operator = operator;
    }

    public OperatorForm getForm() {
        return form;
    }

    public char[] getOperator() {
        return operator;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        OperatorDictionaryEntryKey that = (OperatorDictionaryEntryKey) o;

        if (form != that.form) return false;
        return Arrays.equals(operator, that.operator);
    }

    @Override
    public int hashCode() {
        int result = form != null ? form.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(operator);
        return result;
    }

}
