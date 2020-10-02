package de.be.thaw.font.opentype.gpos.subtable.pairpos;

import de.be.thaw.font.opentype.gpos.subtable.ValueRecord;

import java.util.Optional;

/**
 * Pair positioning sub table representation.
 */
public interface PairPosSubTable {

    /**
     * Get the adjustment between the given pair of glyph IDs.
     *
     * @param leftGlyphID  the ID of the left glyph
     * @param rightGlyphID the ID of the right glyph
     * @return adjustment for the given pair
     */
    Optional<PairPosAdjustment> getAdjustment(int leftGlyphID, int rightGlyphID);

    /**
     * An adjustment of a glyph pair.
     */
    class PairPosAdjustment {

        /**
         * Adjustment for the left glyph of the pair.
         */
        private final ValueRecord left;

        /**
         * Adjustment for the right glyph of the pair.
         */
        private final ValueRecord right;

        public PairPosAdjustment(ValueRecord left, ValueRecord right) {
            this.left = left;
            this.right = right;
        }

        /**
         * Get the adjustment for the left glyph of the pair.
         *
         * @return left glyph adjustment
         */
        public ValueRecord getLeft() {
            return left;
        }

        /**
         * Get the adjustment for the right glyph of the pair.
         *
         * @return right glyph adjustment
         */
        public ValueRecord getRight() {
            return right;
        }

    }

}
