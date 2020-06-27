package de.be.thaw.text.parser.rule.impl;

import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.rule.ParseRule;
import de.be.thaw.text.parser.tree.Node;
import de.be.thaw.text.parser.tree.NodeType;
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
                Node enumeration = new Node(NodeType.ENUMERATION, null);
                node.addChild(enumeration);

                Node item = new Node(NodeType.ENUMERATION_ITEM, token);
                enumeration.addChild(item);

                yield item;
            }
            case ENUMERATION_ITEM -> {
                EnumerationItemStartToken nodeToken = (EnumerationItemStartToken) node.getToken();
                assert nodeToken != null;
                int oldIndent = nodeToken.getIndent();
                int newIndent = ((EnumerationItemStartToken) token).getIndent();

                Node enumerationNode;
                if (oldIndent == newIndent) {
                    // Is the same enumeration level
                    enumerationNode = node.getParent();
                } else if (oldIndent < newIndent) {
                    // Create new enumeration level
                    enumerationNode = new Node(NodeType.ENUMERATION, null);
                    assert node.getParent() != null;
                    node.getParent().addChild(enumerationNode);
                } else {
                    // Go up one enumeration level
                    assert node.getParent() != null;
                    enumerationNode = node.getParent().getParent();
                }

                Node item = new Node(NodeType.ENUMERATION_ITEM, token);
                assert enumerationNode != null;
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
