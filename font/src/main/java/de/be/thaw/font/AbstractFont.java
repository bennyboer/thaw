package de.be.thaw.font;

import de.be.thaw.font.util.CharacterSize;
import de.be.thaw.font.util.KernedSize;
import de.be.thaw.font.util.StringSize;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract font representation.
 */
public abstract class AbstractFont implements ThawFont {

    @Override
    public StringSize getStringSize(String str, double fontSize) throws Exception {
        double width = 0;
        double height = 0;

        double maxAscent = Double.MIN_VALUE;
        double maxDescent = Double.MIN_VALUE;

        int len = str.length();
        for (int i = 0; i < len; i++) {
            int character = str.codePointAt(i);

            int charCount = Character.charCount(character);
            if (charCount > 1) {
                i += charCount - 1;
            }

            CharacterSize characterSize = getCharacterSize(character, fontSize);
            width += characterSize.getWidth();
            height = Math.max(characterSize.getHeight(), height);

            maxAscent = Math.max(characterSize.getAscent(), maxAscent);
            maxDescent = Math.max(characterSize.getDescent(), maxDescent);
        }

        return new StringSize(width, height, maxAscent, maxDescent);
    }

    @Override
    public KernedSize getKernedStringSize(int charBefore, String str, double fontSize) throws Exception {
        double width = 0;
        double height = 0;

        double maxAscent = Double.MIN_VALUE;
        double maxDescent = Double.MIN_VALUE;

        int len = str.length();
        List<Double> kerningAdjustments = new ArrayList<>(len);
        for (int i = 0; i < len; i++) {
            int character = str.codePointAt(i);

            int charCount = Character.charCount(character);
            if (charCount > 1) {
                i += charCount - 1;
            }

            double kerningAdjustment = 0;
            if (charBefore != -1) {
                kerningAdjustment = getKerningAdjustment(charBefore, character, fontSize);
            }
            kerningAdjustments.add(kerningAdjustment);

            CharacterSize characterSize = getCharacterSize(character, fontSize);
            width += characterSize.getWidth() + kerningAdjustment;
            height = Math.max(characterSize.getHeight(), height);

            maxAscent = Math.max(characterSize.getAscent(), maxAscent);
            maxDescent = Math.max(characterSize.getDescent(), maxDescent);

            charBefore = character;
        }

        return new KernedSize(
                width,
                height,
                maxAscent,
                maxDescent,
                kerningAdjustments.stream().mapToDouble(i -> i).toArray()
        );
    }

}
