package de.be.thaw.typeset.knuthplass.paragraph.impl.table;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.table.Table;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.util.Insets;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

/**
 * Paragraph representing a table.
 */
public class TableParagraph extends AbstractParagraph {

    /**
     * The actual table.
     */
    private final Table<ThawTableCell> table;

    /**
     * Margin of the paragraph.
     */
    private final Insets margin;

    /**
     * Padding of the paragraph.
     */
    private final Insets padding;

    /**
     * Caption of the image.
     */
    @Nullable
    private final String caption;

    /**
     * Prefix of the caption.
     * Should be something like "Figure" but is customizable.
     */
    @Nullable
    private final String captionPrefix;

    public TableParagraph(
            Table<ThawTableCell> table,
            double lineWidth,
            DocumentNode node,
            Insets margin,
            Insets padding,
            @Nullable String caption,
            @Nullable String captionPrefix
    ) {
        super(lineWidth, node);

        this.table = table;

        this.margin = margin;
        this.padding = padding;

        this.caption = caption;
        this.captionPrefix = captionPrefix;
    }

    @Override
    public ParagraphType getType() {
        return ParagraphType.TABLE;
    }

    /**
     * Get the table.
     *
     * @return table
     */
    public Table<ThawTableCell> getTable() {
        return table;
    }

    /**
     * Get the margin of the paragraph.
     *
     * @return margin
     */
    public Insets getMargin() {
        return margin;
    }

    /**
     * Get the padding of the paragraph.
     *
     * @return padding
     */
    public Insets getPadding() {
        return padding;
    }

    /**
     * Get the caption of the paragraph.
     *
     * @return caption or empty optional
     */
    public Optional<String> getCaption() {
        return Optional.ofNullable(caption);
    }

    /**
     * Get the caption prefix.
     *
     * @return caption prefix or an empty optional
     */
    public Optional<String> getCaptionPrefix() {
        return Optional.ofNullable(captionPrefix);
    }

}
