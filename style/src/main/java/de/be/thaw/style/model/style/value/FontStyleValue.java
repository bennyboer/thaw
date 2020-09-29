package de.be.thaw.style.model.style.value;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Style value for fonts.
 */
public class FontStyleValue extends AbstractStyleValue {

    /**
     * File of the font.
     */
    @Nullable
    private final File file;

    /**
     * Name of the font.
     */
    @Nullable
    private final String name;

    public FontStyleValue(@NotNull File file) {
        this.file = file;
        this.name = null;
    }

    public FontStyleValue(@NotNull String name) {
        this.name = name;
        this.file = null;
    }

    @Override
    public String value() {
        return name != null ? name : String.format("url(%s)", file.getAbsolutePath());
    }

    @Override
    public File file() {
        return file;
    }

}
