package de.be.thaw.style.model.style.value;

import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.style.model.style.util.FillStyle;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.color.Color;
import de.be.thaw.util.unit.Unit;

import java.util.Optional;

/**
 * Abstract representation of a style value.
 */
public abstract class AbstractStyleValue implements StyleValue {

    @Override
    public boolean booleanValue() {
        throw new UnsupportedOperationException("Could not express style value as boolean");
    }

    @Override
    public int intValue() {
        throw new UnsupportedOperationException("Could not express style value as integer");
    }

    @Override
    public double doubleValue() {
        throw new UnsupportedOperationException("Could not express style value as double");
    }

    @Override
    public Optional<Unit> unit() {
        return Optional.empty();
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

}
