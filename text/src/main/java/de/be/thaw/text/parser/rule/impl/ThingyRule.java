package de.be.thaw.text.parser.rule.impl;

import de.be.thaw.text.model.tree.Node;
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

        node.addChild(new ThingyNode(tt.getName(), tt.getArguments(), tt.getOptions(), tt.getPosition()));

        return node;
    }

}
