package de.be.thaw.math.mathml.typeset.impl.handler;

import de.be.thaw.font.util.KernedSize;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.OperatorNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.OperatorElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.util.MathVariantUtil;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

import java.util.HashMap;
import java.util.Map;

/**
 * Handler dealing with operator nodes.
 */
public class OperatorNodeHandler implements MathMLNodeHandler {

    /**
     * Replacements for certain characters.
     */
    private static final Map<Integer, Character> OPERATOR_REPLACEMENT_MAP = new HashMap<>();

    static {
        OPERATOR_REPLACEMENT_MAP.put((int) '-', '\u2212');
        OPERATOR_REPLACEMENT_MAP.put((int) '*', '\u2217');
        OPERATOR_REPLACEMENT_MAP.put((int) '/', '\u2236');
    }

    @Override
    public String supportedNodeName() {
        return "mo";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        OperatorNode mo = (OperatorNode) node;

        String operator = mo.getOperator();

        boolean isArithmeticOperator = operator.length() == 1 && MathVariantUtil.isArithmeticOperator(operator.charAt(0));

        // Convert operator to the correct font variant (math variant)
        operator = MathVariantUtil.convertStringUsingMathVariant(operator, mo.getMathVariant());

        // Replace certain characters
        StringBuilder builder = new StringBuilder();
        int len = operator.length();
        for (int i = 0; i < len; ) {
            int codePoint = operator.codePointAt(i);
            i += Character.charCount(codePoint);

            if (OPERATOR_REPLACEMENT_MAP.containsKey(codePoint)) {
                builder.append(OPERATOR_REPLACEMENT_MAP.get(codePoint));
            } else {
                builder.append(Character.toChars(codePoint));
            }
        }
        operator = builder.toString();

        // TODO Deal with mathsize (once attribute is parsed)

        KernedSize size;
        try {
            size = ctx.getConfig().getFont().getKernedStringSize(-1, operator, ctx.getLevelAdjustedFontSize());
        } catch (Exception e) {
            throw new TypesetException(e);
        }

        boolean isFirstNode = node.getParent().map(p -> p.getChildren().indexOf(node) == 0).orElse(false);
        boolean isLastNode = node.getParent().map(p -> p.getChildren().indexOf(node) == p.getChildren().size() - 1).orElse(false);
        double leftMargin = isFirstNode || isLastNode || !isArithmeticOperator ? 0 : mo.getLeftSpaceWidth();
        double rightMargin = isFirstNode || isLastNode || !isArithmeticOperator ? 0 : mo.getRightSpaceWidth();

        Position position = new Position(ctx.getCurrentX() + leftMargin, ctx.getCurrentY());
        ctx.setCurrentX(position.getX() + size.getWidth() + rightMargin);

        return new OperatorElement(operator, new Size(size.getWidth(), size.getHeight()), ctx.getLevelAdjustedFontSize(), size.getAscent(), size.getKerningAdjustments(), position);
    }

}
