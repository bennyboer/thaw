package de.be.thaw.style.model.style.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Style value for fonts.
 */
public class FontFamilyStyleValue extends AbstractStyleValue {

    /**
     * Name of the font family.
     */
    private final String familyName;

    /**
     * File of the font.
     */
    @Nullable
    private final File file;

    public FontFamilyStyleValue(String familyName) {
        this(familyName, null);
    }

    public FontFamilyStyleValue(String familyName, @NotNull File file) {
        this.file = file;
        this.familyName = familyName;
    }

    @Override
    public String value() {
        return familyName;
    }

    @Override
    public File file() {
        return file;
    }

}
