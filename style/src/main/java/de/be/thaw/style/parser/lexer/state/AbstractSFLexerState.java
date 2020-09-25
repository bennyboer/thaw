package de.be.thaw.style.parser.lexer.state;

import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;

import java.util.Optional;

/**
 * Abstract lexer state used to perform common actions.
 */
public abstract class AbstractSFLexerState implements SFLexerState {

    @Override
    public void process(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        // Check for comments (can be everywhere!)
        switch (c) {
            case '/' -> checkIfComment(c, ctx);
            default -> doProcess(c, ctx);
        }
    }

    /**
     * Check if the next character is a comment.
     *
     * @param c   the current character that indicates it might be a comment
     * @param ctx the lexer context
     * @throws StyleFormatLexerException in case a problem arises
     */
    private void checkIfComment(char c, SFLexerContext ctx) throws StyleFormatLexerException {
        // Check the next character to see if it is a comment
        Optional<Character> optNextChar = ctx.lookAhead(1);
        boolean isComment = false;
        if (optNextChar.isPresent()) {
            char nextChar = optNextChar.get();
            if (nextChar == '/' || nextChar == '*') {
                isComment = true;
            }
        }

        if (isComment) {
            char nextChar = optNextChar.get();
            if (nextChar == '/') {
                // Push single-line comment state
                ctx.pushState(new SingleLineCommentState());
            } else {
                // Push multi-line comment state
                ctx.pushState(new MultiLineCommentState());
            }
        } else {
            doProcess(c, ctx); // Process as usual
        }
    }

    /**
     * Process the passed character.
     *
     * @param c   to process
     * @param ctx the lexer context
     * @throws StyleFormatLexerException in case we cannot process the character
     */
    public abstract void doProcess(char c, SFLexerContext ctx) throws StyleFormatLexerException;

}
