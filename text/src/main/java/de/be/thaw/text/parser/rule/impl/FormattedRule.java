package de.be.thaw.text.parser.rule.impl;

import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.rule.ParseRule;
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

        Node fmt = new FormattedNode(
                token.getValue(),
                token.getPosition(),
                ((FormattedToken) token).getEmphases(),
                fmtToken.getClassName().orElse(null)
        );

        return switch (node.getType()) {
            case FORMATTED -> {
                // Check if the styles differ
                Node otherNode = node;

                while (otherNode.getType() == NodeType.FORMATTED) {
                    Set<TextEmphasis> newTokenEmphases = new HashSet<>(fmtToken.getEmphases());

                    for (TextEmphasis te : ((FormattedNode) otherNode).getEmphases()) {
                        newTokenEmphases.remove(te);
                    }

                    if (newTokenEmphases.isEmpty()) {
                        otherNode = otherNode.getParent(); // Pop level and try again
                    } else {
                        break; // Push another level with the additional emphasis
                    }
                }

                otherNode.addChild(fmt);

                yield fmt;
            }
            default -> {
                node.addChild(fmt);

                yield fmt;
            }
        };
    }

}
