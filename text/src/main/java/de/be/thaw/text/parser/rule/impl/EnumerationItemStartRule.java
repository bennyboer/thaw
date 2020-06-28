package de.be.thaw.text.parser.rule.impl;

import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.EnumerationItemNode;
import de.be.thaw.text.model.tree.impl.EnumerationNode;
import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.rule.ParseRule;
import de.be.thaw.text.tokenizer.token.EnumerationItemStartToken;
import de.be.thaw.text.tokenizer.token.Token;
import org.jetbrains.annotations.NotNull;

/**
 * Rule that is applied for enumeration item start tokens.
 */
public class EnumerationItemStartRule implements ParseRule {

    @Override
    public Node apply(@NotNull Node node, @NotNull Token token) throws ParseException {
        if (!(token instanceof EnumerationItemStartToken)) {
            throw new ParseException("Rule can only parse tokens of type EnumerationItemStartToken");
        }

        return switch (node.getType()) {
            case BOX -> {
                Node enumeration = new EnumerationNode(1);
                node.addChild(enumeration);

                Node item = new EnumerationItemNode(token.getPosition(), ((EnumerationItemStartToken) token).getIndent());
                enumeration.addChild(item);

                yield item;
            }
            case ENUMERATION_ITEM -> {
                EnumerationItemNode enumItemNode = (EnumerationItemNode) node;

                int oldIndent = enumItemNode.getIndent();
                int newIndent = ((EnumerationItemStartToken) token).getIndent();

                Node enumerationNode;
                if (oldIndent == newIndent) {
                    // Is the same enumeration level
                    enumerationNode = node.getParent();
                } else if (oldIndent < newIndent) {
                    // Create new enumeration level
                    enumerationNode = new EnumerationNode(((EnumerationNode) node.getParent()).getLevel() + 1);
                    node.getParent().addChild(enumerationNode);
                } else {
                    // Go up one enumeration level
                    enumerationNode = node.getParent().getParent();
                }

                Node item = new EnumerationItemNode(token.getPosition(), newIndent);
                enumerationNode.addChild(item);

                yield item;
            }
            default -> throw new ParseException(String.format(
                    "Did not anticipate a token of type '%s' when dealing with a node of type '%s'",
                    token.getType().name(),
                    node.getType().name()
            ));
        };
    }

}
