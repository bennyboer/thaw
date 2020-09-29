package de.be.thaw.text.parser.rule.impl;

import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.FormattedNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.rule.ParseRule;
import de.be.thaw.text.tokenizer.token.ThingyToken;
import de.be.thaw.text.tokenizer.token.Token;
import org.jetbrains.annotations.NotNull;

/**
 * Rule that is applied for thingy tokens.
 */
public class ThingyRule implements ParseRule {

    @Override
    public Node apply(@NotNull Node node, @NotNull Token token) throws ParseException {
        if (!(token instanceof ThingyToken)) {
            throw new ParseException("Rule can only parse tokens of type ThingyToken");
        }

        ThingyToken tt = (ThingyToken) token;
        ThingyNode thingyNode = new ThingyNode(tt.getName(), tt.getArguments(), tt.getOptions(), tt.getPosition());

        if (!tt.getEmphases().isEmpty()) {
            // Add formatted node first
            Node fmt = new FormattedNode(
                    "",
                    token.getPosition(),
                    tt.getEmphases(),
                    tt.getClassName().orElse(null)
            );

            fmt.addChild(thingyNode);
            node.addChild(fmt);
        } else {
            node.addChild(thingyNode);
        }

        return node;
    }

}
