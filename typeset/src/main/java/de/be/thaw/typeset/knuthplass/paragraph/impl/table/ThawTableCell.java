package de.be.thaw.typeset.knuthplass.paragraph.impl.table;

import de.be.thaw.style.model.style.Styles;
import de.be.thaw.table.cell.text.TextCell;
import org.jetbrains.annotations.Nullable;

/**
 * A table cell.
 */
public class ThawTableCell extends TextCell {

    /**
     * Styles to apply when typesetting.
     */
    @Nullable
    private Styles styles;

    public ThawTableCell(String text) {
        super(text);
    }

    @Nullable
    public Styles getStyles() {
        return styles;
    }

    public void setStyles(@Nullable Styles styles) {
        this.styles = styles;
    }

}
