package de.be.thaw.text.parser.rule.impl;

import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.rule.ParseRule;
import de.be.thaw.text.parser.tree.Node;
import de.be.thaw.text.parser.tree.NodeType;
import de.be.thaw.text.tokenizer.token.EmptyLineToken;
import de.be.thaw.text.tokenizer.token.Token;
import org.jetbrains.annotations.NotNull;

/**
 * Rule that is applied for empty line tokens.
 */
public class EmptyLineRule implements ParseRule {

    @Override
    public Node apply(@NotNull Node node, @NotNull Token token) throws ParseException {
        if (!(token instanceof EmptyLineToken)) {
            throw new ParseException("Rule can only parse tokens of type EmptyLineToken");
        }

        // Find the root node
        while (!node.isRoot()) {
            node = node.getParent();
        }

        Node box = new Node(NodeType.BOX, null);
        node.addChild(box);

        return box;
    }

}
