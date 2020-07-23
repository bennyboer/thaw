package de.be.thaw.font;

import de.be.thaw.font.util.KernedSize;
import de.be.thaw.util.Size;

/**
 * Description of a font in the Thaw project.
 */
public interface ThawFont {

    /**
     * Get the size for the passed character.
     *
     * @param character to get size for
     * @param fontSize  the font size
     * @return size
     * @throws Exception in case the character size could not be determined
     */
    Size getCharacterSize(int character, double fontSize) throws Exception;

    /**
     * Get the size for the passed string.
     *
     * @param str to get size for
     * @return size
     * @throws Exception in case the string size could not be determined
     */
    Size getStringSize(String str, double fontSize) throws Exception;

    /**
     * Get the size for the passed string with kerning adjustments applied.
     *
     * @param charBefore the character before the passed string (if any), else pass -1
     * @param str        to get size for
     * @return size
     * @throws Exception in case the string size could not be determined
     */
    KernedSize getKernedStringSize(int charBefore, String str, double fontSize) throws Exception;

    /**
     * Get the kerning adjustment between the passed left and right character.
     *
     * @param leftChar  the left character of the kerning pair
     * @param rightChar the right character of the kerning pair
     * @param fontSize  the font size
     * @return kerning adjustment
     */
    double getKerningAdjustment(int leftChar, int rightChar, double fontSize);

}
