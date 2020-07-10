package de.be.thaw.typeset.knuthplass.config.util;

import de.be.thaw.core.document.node.DocumentNode;

/**
 * Supplier for font details needed to properly typeset text.
 */
public interface FontDetailsSupplier {

    /**
     * Get the width of the passed string.
     *
     * @param node       the passed string belongs to
     * @param charBefore optional character before the passed string (may be -1)
     * @param str        to get width for
     * @return width
     * @throws Exception in case the string width could not be determined
     */
    StringMetrics measureString(DocumentNode node, int charBefore, String str) throws Exception;

    /**
     * Get the space width that applies to the passed node.
     *
     * @param node to get space width for
     * @return width of a space
     * @throws Exception in case the space width could not be determined
     */
    double getSpaceWidth(DocumentNode node) throws Exception;

    /**
     * Collective metrics when measuring a string.
     */
    public static class StringMetrics {

        /**
         * Width of the string.
         */
        private final double width;

        /**
         * Height of the string.
         */
        private final double height;

        /**
         * Adjustments due to kerning.
         */
        private final double[] kerningAdjustments;

        /**
         * Font size the results were calculated with.
         */
        private final double fontSize;

        public StringMetrics(double width, double height, double[] kerningAdjustments, double fontSize) {
            this.width = width;
            this.height = height;
            this.kerningAdjustments = kerningAdjustments;
            this.fontSize = fontSize;
        }

        public double getWidth() {
            return width;
        }

        public double getHeight() {
            return height;
        }

        public double[] getKerningAdjustments() {
            return kerningAdjustments;
        }

        public double getFontSize() {
            return fontSize;
        }

    }

}
