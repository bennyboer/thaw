package de.be.thaw.text.tokenizer.state.util;

import de.be.thaw.text.tokenizer.TokenizingContext;

/**
 * Helper methods to help during tokenizing in the states.
 */
public class StateUtil {

    /**
     * Check if the current char is the first non-whitespace character in the line.
     *
     * @param ctx to check the buffer of
     * @return whether the current character is the first non-whitespace char in line
     */
    public static boolean isFirstNonWhiteSpaceCharacterInLine(TokenizingContext ctx) {
        return lastNWhiteSpaceChars(ctx, ctx.getCurrentPos() - 2);
    }

    /**
     * Check if the last N characters in the buffer are white space characters.
     *
     * @param ctx to check the buffer from
     * @return whether last N characters are white spaces
     */
    public static boolean lastNWhiteSpaceChars(TokenizingContext ctx, int n) {
        boolean result = true;

        int bufferLen = ctx.getBuffer().length();

        for (int i = bufferLen - 1; i >= Math.max(0, bufferLen - n); i--) {
            if (ctx.getBuffer().charAt(i) != ' ') {
                result = false;
            }
        }

        return result;
    }

}
