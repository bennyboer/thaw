package de.be.thaw.style.model.style.value;

import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.style.model.style.util.FillStyle;
import de.be.thaw.style.model.style.util.list.ListStyleType;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.VerticalAlignment;
import de.be.thaw.util.color.Color;
import de.be.thaw.util.unit.Unit;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Abstract representation of a style value.
 */
public abstract class AbstractStyleValue implements StyleValue {

    @Override
    public boolean booleanValue() {
        throw new UnsupportedOperationException("Could not express style value as boolean");
    }

    @Override
    public int intValue(@Nullable Unit targetUnit) {
        throw new UnsupportedOperationException("Could not express style value as integer");
    }

    @Override
    public double doubleValue(@Nullable Unit targetUnit) {
        throw new UnsupportedOperationException("Could not express style value as double");
    }

    @Override
    public Unit unit() {
        throw new UnsupportedOperationException("The style value does not have a unit");
    }

    @Override
    public Color colorValue() {
        throw new UnsupportedOperationException("Could not express style value as color");
    }

    @Override
    public HorizontalAlignment horizontalAlignment() {
        throw new UnsupportedOperationException("Could not express style value as horizontal alignment");
    }

    @Override
    public VerticalAlignment verticalAlignment() {
        throw new UnsupportedOperationException("Could not express style value as vertical alignment");
    }

    @Override
    public FontVariant fontVariant() {
        throw new UnsupportedOperationException("Could not express style value as font variant");
    }

    @Override
    public KerningMode kerningMode() {
        throw new UnsupportedOperationException("Could not express style value as kerning mode");
    }

    @Override
    public FillStyle fillStyle() {
        throw new UnsupportedOperationException("Could not express style value as fill style");
    }

    @Override
    public ListStyleType listStyleType() {
        throw new UnsupportedOperationException("Could not express style value as list style type");
    }

    @Override
    public File file() {
        throw new UnsupportedOperationException("Could not express style value as file");
    }

}
