package de.be.thaw.math.mathml.typeset.impl.handler.impl;

import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.impl.PaddedNode;
import de.be.thaw.math.mathml.typeset.element.MathElement;
import de.be.thaw.math.mathml.typeset.element.impl.PaddedElement;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.MathTypesetContext;
import de.be.thaw.math.mathml.typeset.impl.handler.MathMLNodeHandler;
import de.be.thaw.math.mathml.typeset.impl.handler.MathNodeHandlers;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Handler dealing with a padded node.
 */
public class PaddedNodeHandler implements MathMLNodeHandler {

    @Override
    public String supportedNodeName() {
        return "mpadded";
    }

    @Override
    public MathElement handle(MathMLNode node, MathTypesetContext ctx) throws TypesetException {
        PaddedNode paddedNode = (PaddedNode) node;

        double x = ctx.getCurrentX();
        double y = ctx.getCurrentY();

        ctx.setCurrentX(0);
        ctx.setCurrentY(0);

        // First and foremost typeset the child element
        MathElement child = MathNodeHandlers.getHandler(paddedNode.getChildren().get(0).getName())
                .handle(paddedNode.getChildren().get(0), ctx);

        if (paddedNode.getXAdjustment() != null) {
            x += relativeAdjustmentExpressionToDouble(paddedNode.getXAdjustment(), child.getSize(), child.getBaseline(), child.getPosition().getX());
            child.setPosition(new Position(
                    0,
                    child.getPosition().getY()
            ));
        }

        if (paddedNode.getYAdjustment() != null) {
            y += relativeAdjustmentExpressionToDouble(paddedNode.getYAdjustment(), child.getSize(), child.getBaseline(), child.getPosition().getY());
            child.setPosition(new Position(
                    child.getPosition().getX(),
                    0
            ));
        }

        Position position = new Position(x, y);

        double width = child.getSize().getWidth();
        if (paddedNode.getWidthAdjustment() != null) {
            width = relativeAdjustmentExpressionToDouble(paddedNode.getWidthAdjustment(), child.getSize(), child.getBaseline(), width);
        }

        double baseline = child.getBaseline();
        if (paddedNode.getHeightAdjustment() != null) {
            baseline = relativeAdjustmentExpressionToDouble(paddedNode.getHeightAdjustment(), child.getSize(), child.getBaseline(), baseline);
        }

        double depth = child.getSize().getHeight() - child.getBaseline();
        if (paddedNode.getDepthAdjustment() != null) {
            depth = relativeAdjustmentExpressionToDouble(paddedNode.getDepthAdjustment(), child.getSize(), child.getBaseline(), depth);
        }

        // Create padded element
        PaddedElement paddedElement = new PaddedElement(position, width, baseline, depth);

        // Add child
        paddedElement.addChild(child);

        // Set correct position context
        ctx.setCurrentX(position.getX() + paddedElement.getSize().getWidth());

        return paddedElement;
    }

    /**
     * Convert the passed adjustment expression to a double value.
     * For example "2width" would be two times the current elements width.
     * Also allowed would be something like "+4" which would mean the current value + 4.
     *
     * @param adjustmentExpression to convert
     * @param size                 of the current element
     * @param baseline             of the current element
     * @param currentValue         to adjust with the passed expression
     * @return the adjusted value
     */
    private double relativeAdjustmentExpressionToDouble(String adjustmentExpression, Size size, double baseline, double currentValue) throws TypesetException {
        List<Token> tokens = adjustmentExpressionToTokenList(adjustmentExpression);

        Map<String, Double> keywordValueLookup = new HashMap<>();
        keywordValueLookup.put("width", size.getWidth());
        keywordValueLookup.put("height", baseline);
        keywordValueLookup.put("depth", size.getHeight() - baseline);

        double newValue = currentValue;
        AtomicBoolean isAdd = new AtomicBoolean(false);
        AtomicBoolean isSubtract = new AtomicBoolean(false);
        for (int i = 0; i < tokens.size(); i++) {
            Token token = tokens.get(i);

            switch (token.type) {
                case NUMBER -> {
                    if (isAdd.get()) {
                        isAdd.set(false);
                        newValue += Double.parseDouble(token.value);
                    } else if (isSubtract.get()) {
                        isSubtract.set(false);
                        newValue -= Double.parseDouble(token.value);
                    } else {
                        newValue = Double.parseDouble(token.value);
                    }
                }
                case KEYWORD -> {
                    if (!keywordValueLookup.containsKey(token.value)) {
                        throw new TypesetException(String.format("The <mpadded> element does not allow a attribute value keyword '%s'", token.value));
                    }

                    if (isAdd.get()) {
                        isAdd.set(false);
                        newValue += keywordValueLookup.get(token.value);
                    } else if (isSubtract.get()) {
                        isSubtract.set(false);
                        newValue -= keywordValueLookup.get(token.value);
                    } else if (i > 0 && tokens.get(i - 1).type == TokenType.NUMBER) {
                        // Act like multiplication
                        newValue *= keywordValueLookup.get(token.value);
                    } else {
                        newValue = keywordValueLookup.get(token.value);
                    }
                }
                case OPERATOR -> {
                    char operator = token.value.charAt(0);
                    switch (operator) {
                        case '+' -> isAdd.set(true);
                        case '-' -> isSubtract.set(true);
                    }
                }
            }
        }

        return newValue;
    }

    /**
     * Convert the passed adjustment expression to a list of tokens.
     *
     * @param adjustmentExpression to convert
     * @return token list
     */
    private List<Token> adjustmentExpressionToTokenList(String adjustmentExpression) throws TypesetException {
        List<Token> tokens = new ArrayList<>();

        TokenType current = null;
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < adjustmentExpression.length(); i++) {
            char c = adjustmentExpression.charAt(i);

            if (Character.isDigit(c)) {
                if (current == null) {
                    current = TokenType.NUMBER;
                    buffer.append(c);
                } else {
                    if (current == TokenType.NUMBER) {
                        buffer.append(c);
                    } else {
                        tokens.add(new Token(current, buffer.toString()));
                        buffer.setLength(0);
                        buffer.append(c);
                        current = TokenType.NUMBER;
                    }
                }
            } else if (c == '+' || c == '-') {
                if (current == null) {
                    current = TokenType.OPERATOR;
                    buffer.append(c);
                } else {
                    if (current == TokenType.OPERATOR) {
                        throw new TypesetException("Operator has to be only one character and split by another token type");
                    } else {
                        tokens.add(new Token(current, buffer.toString()));
                        buffer.setLength(0);
                        buffer.append(c);
                        current = TokenType.OPERATOR;
                    }
                }
            } else {
                if (current == null) {
                    current = TokenType.KEYWORD;
                    buffer.append(c);
                } else {
                    if (current == TokenType.KEYWORD) {
                        buffer.append(c);
                    } else {
                        tokens.add(new Token(current, buffer.toString()));
                        buffer.setLength(0);
                        buffer.append(c);
                        current = TokenType.KEYWORD;
                    }
                }
            }
        }

        if (buffer.length() > 0 && current != null) {
            tokens.add(new Token(current, buffer.toString()));
        }

        return tokens;
    }

    private enum TokenType {
        OPERATOR,
        NUMBER,
        KEYWORD
    }

    private static class Token {
        TokenType type;
        String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }
    }

}
