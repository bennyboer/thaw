package de.be.thaw.text.parser;

import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.RootNode;
import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.text.parser.rule.ParseRule;
import de.be.thaw.text.parser.rule.impl.EmptyLineRule;
import de.be.thaw.text.parser.rule.impl.EnumerationItemStartRule;
import de.be.thaw.text.parser.rule.impl.FormattedRule;
import de.be.thaw.text.parser.rule.impl.TextRule;
import de.be.thaw.text.parser.rule.impl.ThingyRule;
import de.be.thaw.text.tokenizer.TextTokenizer;
import de.be.thaw.text.tokenizer.exception.TokenizeException;
import de.be.thaw.text.tokenizer.token.Token;
import de.be.thaw.text.tokenizer.token.TokenType;
import de.be.thaw.text.tokenizer.util.result.Result;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;

/**
 * Parser for the thaw document text format.
 */
public class TextParser {

    /**
     * Map of token types to rules that should process them.
     */
    private static final Map<TokenType, ParseRule> RULES = new HashMap<>();

    static {
        RULES.put(TokenType.TEXT, new TextRule());
        RULES.put(TokenType.EMPTY_LINE, new EmptyLineRule());
        RULES.put(TokenType.THINGY, new ThingyRule());
        RULES.put(TokenType.FORMATTED, new FormattedRule());
        RULES.put(TokenType.ENUMERATION_ITEM_START, new EnumerationItemStartRule());
    }

    /**
     * Parse the text readable using the passed reader.
     *
     * @param reader to use during parsing
     * @return the parsed text model
     * @throws ParseException in case parsing failed
     */
    public TextModel parse(Reader reader) throws ParseException {
        RootNode root = new RootNode();

        Node currentNode = new BoxNode();
        root.addChild(currentNode);

        try {
            TextTokenizer tokenizer = new TextTokenizer(reader);

            while (tokenizer.hasNext()) {
                Result<Token, TokenizeException> r = tokenizer.next();
                if (r.isError()) {
                    throw new ParseException(r.error());
                }

                currentNode = onNextToken(currentNode, r.result());
            }
        } catch (TokenizeException e) {
            throw new ParseException(e);
        }

        return new TextModel(root);
    }

    /**
     * Process the next token.
     *
     * @param node  the current node
     * @param token to process
     * @return the next node
     * @throws ParseException in case something went wrong during parsing
     */
    private Node onNextToken(Node node, Token token) throws ParseException {
        ParseRule rule = RULES.get(token.getType());

        if (rule == null) {
            throw new ParseException(String.format(
                    "Token type '%s' does not have a parsing rule associated with",
                    token.getType().name()
            ));
        }

        return rule.apply(node, token);
    }

}
