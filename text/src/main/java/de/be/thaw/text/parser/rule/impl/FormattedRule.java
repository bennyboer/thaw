package de.be.thaw.text.parser.rule.impl;

import de.be.thaw.text.model.element.emphasis.TextEmphasis;
import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.rule.ParseRule;
import de.be.thaw.text.parser.tree.Node;
import de.be.thaw.text.parser.tree.NodeType;
import de.be.thaw.text.tokenizer.token.FormattedToken;
import de.be.thaw.text.tokenizer.token.Token;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;

/**
 * Rule that is applied for formatted tokens.
 */
public class FormattedRule implements ParseRule {

    @Override
    public Node apply(@NotNull Node node, @NotNull Token token) throws ParseException {
        if (!(token instanceof FormattedToken)) {
            throw new ParseException("Rule can only parse tokens of type FormattedToken");
        }
        FormattedToken fmtToken = (FormattedToken) token;

        Node fmt = new Node(NodeType.FORMATTED, token);

        return switch (node.getType()) {
            case FORMATTED -> {
                // Check if the styles differ
                Token otherToken = node.getToken();
                while (otherToken instanceof FormattedToken) {
                    Set<TextEmphasis> newTokenEmphases = new HashSet<>(fmtToken.getEmphases());
                    for (TextEmphasis te : ((FormattedToken) otherToken).getEmphases()) {
                        newTokenEmphases.remove(te);
                    }

                    if (newTokenEmphases.isEmpty()) {
                        // Pop level and try again
                        node = node.getParent();
                        otherToken = node.getToken();
                    } else {
                        // Push another level with the additional emphasis
                        break;
                    }
                }

                node.addChild(fmt);

                yield fmt;
            }
            default -> {
                node.addChild(fmt);

                yield fmt;
            }
        };
    }

}
