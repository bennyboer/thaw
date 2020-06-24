package de.be.thaw.text.parser.rule.impl;

import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.rule.ParseRule;
import de.be.thaw.text.parser.tree.Node;
import de.be.thaw.text.parser.tree.NodeType;
import de.be.thaw.text.tokenizer.token.TextToken;
import de.be.thaw.text.tokenizer.token.Token;
import org.jetbrains.annotations.NotNull;

/**
 * Rule that is applied for text tokens.
 */
public class TextRule implements ParseRule {

    @Override
    public Node apply(@NotNull Node node, @NotNull Token token) throws ParseException {
        if (!(token instanceof TextToken)) {
            throw new ParseException("Rule can only parse tokens of type TextToken");
        }

        Node text = new Node(NodeType.TEXT, token);

        return switch (node.getType()) {
            case FORMATTED -> {
                // No more formatted text
                while (node.getType() == NodeType.FORMATTED) {
                    node = node.getParent();
                }

                node.addChild(text);

                yield node;
            }
            default -> {
                node.addChild(text);

                yield node;
            }
        };
    }

}
