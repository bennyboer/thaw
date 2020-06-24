package de.be.thaw.text.parser.rule;

import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.tree.Node;
import de.be.thaw.text.tokenizer.token.Token;
import org.jetbrains.annotations.NotNull;

/**
 * Rule used to parse the text.
 */
public interface ParseRule {

    /**
     * Apply the rule on the passed node and token.
     *
     * @param node  the current node
     * @param token to add
     * @return the next node
     * @throws ParseException in case something went wrong
     */
    Node apply(@NotNull Node node, @NotNull Token token) throws ParseException;

}
