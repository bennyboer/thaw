package de.be.thaw.style.parser.lexer.impl;

import de.be.thaw.style.parser.lexer.StyleFormatLexer;
import de.be.thaw.style.parser.lexer.context.SFLexerContext;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatToken;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Optional;
import java.util.Queue;

/**
 * The default style format lexer.
 */
public class DefaultStyleFormatLexer implements StyleFormatLexer {

    @Override
    public List<StyleFormatToken> process(Reader reader) throws StyleFormatLexerException {
        List<StyleFormatToken> result = new ArrayList<>();
        Queue<Character> lookAheadBuffer = new LinkedList<>();
        SFLexerContext ctx = new SFLexerContext(result::add, offset -> {
            int next = -1;
            while (offset > 0) {
                try {
                    next = reader.read();
                } catch (IOException e) {
                    return Optional.empty();
                }

                if (next == -1) {
                    return Optional.empty();
                }

                // Buffer char as it has already been read
                lookAheadBuffer.add((char) next);

                offset--;
            }

            return Optional.of((char) next);
        });

        try {
            int nextChar = reader.read();
            while (nextChar != -1) {
                // Process next char
                ctx.process((char) nextChar);

                // Check if we have buffered some characters since they have been looked ahead already
                if (lookAheadBuffer.isEmpty()) {
                    nextChar = reader.read();
                } else {
                    // Read from look ahead buffer instead of reader
                    nextChar = lookAheadBuffer.poll();
                }
            }
        } catch (IOException e) {
            throw new StyleFormatLexerException("Reading the next character failed", e, ctx.getCurrentPosition());
        }

        ctx.finish(); // Tell context that reading is done

        return result;
    }

}
