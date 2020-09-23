package de.be.thaw.typeset.knuthplass.util;

import de.be.thaw.util.Size;

import java.util.function.Supplier;

/**
 * Size that is determined lazily.
 */
public class LazySize extends Size {

    /**
     * Supplier delivering the current width.
     */
    private final Supplier<Double> widthSupplier;

    /**
     * Supplier delivering the current height.
     */
    private final Supplier<Double> heightSupplier;

    public LazySize(Supplier<Double> widthSupplier, Supplier<Double> heightSupplier) {
        super(widthSupplier.get(), heightSupplier.get());

        this.widthSupplier = widthSupplier;
        this.heightSupplier = heightSupplier;
    }

    @Override
    public double getWidth() {
        return widthSupplier.get();
    }

    @Override
    public double getHeight() {
        return heightSupplier.get();
    }

}
