package de.be.thaw.typeset.knuthplass.paragraph.handler.impl.table;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.table.Table;
import de.be.thaw.table.cell.CellRange;
import de.be.thaw.table.cell.CellSpan;
import de.be.thaw.table.impl.DefaultTable;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.AbstractParagraphHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.table.TableParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.table.ThawTableCell;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.page.impl.RectangleElement;
import de.be.thaw.typeset.page.util.LineStyle;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.Bounds;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import de.be.thaw.util.VerticalAlignment;
import de.be.thaw.util.color.Color;
import de.be.thaw.util.unit.Unit;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Handler for table paragraphs.
 */
public class TableParagraphHandler extends AbstractParagraphHandler {

    /**
     * The default caption prefix for tables.
     */
    private static final String DEFAULT_TABLE_CAPTION_PREFIX = "Table";

    /**
     * Style types that are forbidden for the cell typesetting process.
     */
    private static final Set<StyleType> FORBIDDEN_CELL_TYPESETTING_STYLE_TYPES = Set.of(
            StyleType.BORDER_TOP_STYLE,
            StyleType.BORDER_TOP_COLOR,
            StyleType.BORDER_TOP_WIDTH,

            StyleType.BORDER_BOTTOM_STYLE,
            StyleType.BORDER_BOTTOM_COLOR,
            StyleType.BORDER_BOTTOM_WIDTH,

            StyleType.BORDER_LEFT_STYLE,
            StyleType.BORDER_LEFT_COLOR,
            StyleType.BORDER_LEFT_WIDTH,

            StyleType.BORDER_RIGHT_STYLE,
            StyleType.BORDER_RIGHT_COLOR,
            StyleType.BORDER_RIGHT_WIDTH,

            StyleType.BACKGROUND_COLOR,

            StyleType.MARGIN_TOP,
            StyleType.MARGIN_BOTTOM,
            StyleType.MARGIN_LEFT,
            StyleType.MARGIN_RIGHT,

            StyleType.PADDING_TOP,
            StyleType.PADDING_BOTTOM,
            StyleType.PADDING_LEFT,
            StyleType.PADDING_RIGHT
    );

    @Override
    public ParagraphType supportedType() {
        return ParagraphType.TABLE;
    }

    @Override
    public void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException {
        TableParagraph tableParagraph = (TableParagraph) paragraph;

        Insets margin = tableParagraph.getMargin();
        Insets padding = tableParagraph.getPadding();

        Table<ThawTableCell> table = tableParagraph.getTable();

        // Fetch rows with specially set (fixed) row sizes
        Set<Integer> fixedSizedRows = new HashSet<>();
        for (int row = 1; row <= table.getRows(); row++) {
            if (table.getRowSize(row) != ((DefaultTable<ThawTableCell>) table).getDefaultRowSize()) {
                fixedSizedRows.add(row);
            }
        }

        List<ThawTableCell> cells = table.getCells();
        TypeSetCellInfo[] typeSetCellInfos = new TypeSetCellInfo[cells.size()];
        for (int i = 0; i < cells.size(); i++) {
            ThawTableCell cell = cells.get(i);

            // Typeset the cell (does not yet add it to the page)
            TypeSetCellInfo info = typeSetCell(cell, table.getBounds(cell.getSpan()), ctx);
            typeSetCellInfos[i] = info;

            // Only set a new row size if the current row size is not specially set (fixed size)
            if (!fixedSizedRows.contains(cell.getSpan().getRowRange().getEnd())) {
                double neededRowHeight = info.getTypeSetSize().getHeight() + info.getMargin().getTop() + info.getMargin().getBottom() + info.getPadding().getTop() + info.getPadding().getBottom();
                if (cell.getSpan().getRowRange().getStart() != cell.getSpan().getRowRange().getEnd()) {
                    // Cell spans over multiple rows -> only change the last row size
                    neededRowHeight -= table.getBounds(new CellSpan(
                            new CellRange(cell.getSpan().getRowRange().getStart(), cell.getSpan().getRowRange().getEnd() - 1),
                            cell.getSpan().getColumnRange()
                    )).getSize().getHeight();
                }

                if (table.getRowSize(cell.getSpan().getRowRange().getEnd()) < neededRowHeight) {
                    ((DefaultTable<ThawTableCell>) table).setRowSize(cell.getSpan().getRowRange().getEnd(), neededRowHeight);
                }
            }
        }

        // Now align, add backgrounds, add borders and add the cells actually to the current page
        double yOffsetCorrector = margin.getTop() + padding.getTop(); // y-offset corrector needed when the table does not fit on the current page
        for (int i = 0; i < cells.size(); i++) {
            ThawTableCell cell = cells.get(i);
            TypeSetCellInfo info = typeSetCellInfos[i];

            // Fetch final row size
            Bounds bounds = table.getBounds(cell.getSpan());

            // Check if cell fits on the current page
            double availableHeight = ctx.getAvailableHeight();
            double sizeDiff = availableHeight - (bounds.getPosition().getY() + bounds.getSize().getHeight() + yOffsetCorrector);
            if (sizeDiff < 0) {
                ctx.pushPage();

                yOffsetCorrector -= bounds.getPosition().getY() + yOffsetCorrector;
            }

            // Re-layout elements for the current page
            for (Element element : info.getElements()) {
                AbstractElement abstractElement = (AbstractElement) element;
                abstractElement.setPosition(new Position(
                        abstractElement.getPosition().getX() + margin.getLeft() + padding.getLeft(),
                        abstractElement.getPosition().getY() + yOffsetCorrector
                ));
            }

            // Add background and borders
            RectangleElement rectangleElement = new RectangleElement(
                    ctx.getCurrentPageNumber(),
                    new Size(
                            info.getTypeSetSize().getWidth() + info.getPadding().getLeft() + info.getPadding().getRight(),
                            bounds.getSize().getHeight() - info.getMargin().getTop() - info.getMargin().getBottom()
                    ),
                    new Position(
                            ctx.getPositionContext().getX() + bounds.getPosition().getX() + info.getMargin().getLeft() + margin.getLeft() + padding.getLeft(),
                            ctx.getPositionContext().getY() + bounds.getPosition().getY() + info.getMargin().getTop() + yOffsetCorrector
                    )
            );
            rectangleElement.setFillColor(info.getBackgroundColor());
            rectangleElement.setBorderStyles(info.getBorderStyles());
            rectangleElement.setBorderWidths(info.getBorderWidths());
            rectangleElement.setStrokeColors(info.getBorderColors());
            rectangleElement.setBorderRadius(info.getBorderRadius());
            ctx.pushPageElement(rectangleElement);

            // Vertically align element (if necessary)
            if (info.getVerticalAlignment() == VerticalAlignment.CENTER) {
                for (Element element : info.getElements()) {
                    AbstractElement abstractElement = (AbstractElement) element;
                    abstractElement.setPosition(new Position(
                            abstractElement.getPosition().getX(),
                            abstractElement.getPosition().getY() + (bounds.getSize().getHeight() - info.getTypeSetSize().getHeight()) / 2
                    ));
                }
            } else if (info.getVerticalAlignment() == VerticalAlignment.BOTTOM) {
                for (Element element : info.getElements()) {
                    AbstractElement abstractElement = (AbstractElement) element;
                    abstractElement.setPosition(new Position(
                            abstractElement.getPosition().getX(),
                            abstractElement.getPosition().getY() + (bounds.getSize().getHeight() - info.getTypeSetSize().getHeight())
                    ));
                }
            }

            // Add typeset cell to page
            ctx.getCurrentPageElements().addAll(info.getElements());
        }

        ctx.getPositionContext().increaseY(table.getSize().getHeight() + yOffsetCorrector + margin.getBottom() + padding.getBottom());

        // Deal with the table paragraphs caption (if any).
        if (tableParagraph.getCaption().isPresent()) {
            addCaption(
                    tableParagraph.getCaption().orElseThrow(),
                    tableParagraph.getCaptionPrefix().isPresent() ?
                            tableParagraph.getCaptionPrefix().orElseThrow() :
                            (String) ctx.getConfig().getProperties().getOrDefault("table.caption.prefix", DEFAULT_TABLE_CAPTION_PREFIX),
                    tableParagraph,
                    false,
                    ctx.getPositionContext().getY() - margin.getBottom(),
                    ctx.getPositionContext().getX() + margin.getLeft() + padding.getLeft(),
                    table.getSize().getWidth(),
                    margin,
                    padding,
                    ctx
            );
        }
    }

    /**
     * Typeset the passed cell.
     *
     * @param cell       to typeset contents of
     * @param cellBounds bounds of the cell
     * @param ctx        the typesetting context
     * @return the typeset cell information yet to be added to pages
     * @throws TypeSettingException in case the cell could not be typeset
     */
    private TypeSetCellInfo typeSetCell(ThawTableCell cell, Bounds cellBounds, TypeSettingContext ctx) throws TypeSettingException {
        // Fetch needed styles for the cell
        double marginTop = 0;
        double marginBottom = 0;
        double marginLeft = 0;
        double marginRight = 0;

        double paddingTop = 0;
        double paddingBottom = 0;
        double paddingLeft = 0;
        double paddingRight = 0;

        double borderLeftWidth = 0;
        double borderRightWidth = 0;
        double borderTopWidth = 0;
        double borderBottomWidth = 0;

        LineStyle borderLeftStyle = LineStyle.SOLID;
        LineStyle borderRightStyle = LineStyle.SOLID;
        LineStyle borderTopStyle = LineStyle.SOLID;
        LineStyle borderBottomStyle = LineStyle.SOLID;

        Color borderLeftColor = new Color(1.0, 1.0, 1.0);
        Color borderRightColor = new Color(1.0, 1.0, 1.0);
        Color borderTopColor = new Color(1.0, 1.0, 1.0);
        Color borderBottomColor = new Color(1.0, 1.0, 1.0);

        Color backgroundColor = new Color(1.0, 1.0, 1.0, 0.0);

        VerticalAlignment verticalAlignment = VerticalAlignment.TOP;

        Insets borderRadius = new Insets(0);

        if (cell.getStyles() != null) {
            marginTop = cell.getStyles().resolve(StyleType.MARGIN_TOP).orElseThrow().doubleValue(Unit.POINTS);
            marginBottom = cell.getStyles().resolve(StyleType.MARGIN_BOTTOM).orElseThrow().doubleValue(Unit.POINTS);
            marginLeft = cell.getStyles().resolve(StyleType.MARGIN_LEFT).orElseThrow().doubleValue(Unit.POINTS);
            marginRight = cell.getStyles().resolve(StyleType.MARGIN_RIGHT).orElseThrow().doubleValue(Unit.POINTS);

            paddingTop = cell.getStyles().resolve(StyleType.PADDING_TOP).orElseThrow().doubleValue(Unit.POINTS);
            paddingBottom = cell.getStyles().resolve(StyleType.PADDING_BOTTOM).orElseThrow().doubleValue(Unit.POINTS);
            paddingLeft = cell.getStyles().resolve(StyleType.PADDING_LEFT).orElseThrow().doubleValue(Unit.POINTS);
            paddingRight = cell.getStyles().resolve(StyleType.PADDING_RIGHT).orElseThrow().doubleValue(Unit.POINTS);

            borderLeftWidth = cell.getStyles().resolve(StyleType.BORDER_LEFT_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0);
            borderRightWidth = cell.getStyles().resolve(StyleType.BORDER_RIGHT_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0);
            borderTopWidth = cell.getStyles().resolve(StyleType.BORDER_TOP_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0);
            borderBottomWidth = cell.getStyles().resolve(StyleType.BORDER_BOTTOM_WIDTH).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0);

            borderLeftColor = cell.getStyles().resolve(StyleType.BORDER_LEFT_COLOR).map(StyleValue::colorValue).orElse(borderLeftColor);
            borderRightColor = cell.getStyles().resolve(StyleType.BORDER_RIGHT_COLOR).map(StyleValue::colorValue).orElse(borderRightColor);
            borderTopColor = cell.getStyles().resolve(StyleType.BORDER_TOP_COLOR).map(StyleValue::colorValue).orElse(borderTopColor);
            borderBottomColor = cell.getStyles().resolve(StyleType.BORDER_BOTTOM_COLOR).map(StyleValue::colorValue).orElse(borderBottomColor);

            borderLeftStyle = cell.getStyles().resolve(StyleType.BORDER_LEFT_STYLE).map(StyleValue::fillStyle).map(fillStyle -> LineStyle.valueOf(fillStyle.name())).orElse(borderLeftStyle);
            borderRightStyle = cell.getStyles().resolve(StyleType.BORDER_RIGHT_STYLE).map(StyleValue::fillStyle).map(fillStyle -> LineStyle.valueOf(fillStyle.name())).orElse(borderRightStyle);
            borderTopStyle = cell.getStyles().resolve(StyleType.BORDER_TOP_STYLE).map(StyleValue::fillStyle).map(fillStyle -> LineStyle.valueOf(fillStyle.name())).orElse(borderTopStyle);
            borderBottomStyle = cell.getStyles().resolve(StyleType.BORDER_BOTTOM_STYLE).map(StyleValue::fillStyle).map(fillStyle -> LineStyle.valueOf(fillStyle.name())).orElse(borderBottomStyle);

            backgroundColor = cell.getStyles().resolve(StyleType.BACKGROUND_COLOR).map(StyleValue::colorValue).orElse(backgroundColor);

            verticalAlignment = cell.getStyles().resolve(StyleType.VERTICAL_ALIGN).map(StyleValue::verticalAlignment).orElse(verticalAlignment);

            double borderRadiusTop = cell.getStyles().resolve(StyleType.BORDER_RADIUS_TOP).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0);
            double borderRadiusBottom = cell.getStyles().resolve(StyleType.BORDER_RADIUS_BOTTOM).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0);
            double borderRadiusLeft = cell.getStyles().resolve(StyleType.BORDER_RADIUS_LEFT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0);
            double borderRadiusRight = cell.getStyles().resolve(StyleType.BORDER_RADIUS_RIGHT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0);

            borderRadius = new Insets(borderRadiusTop, borderRadiusRight, borderRadiusBottom, borderRadiusLeft);
        }

        // Create style model for the cell
        Map<StyleType, StyleValue> collectiveStyles = new HashMap<>();
        if (cell.getStyles() != null) {
            for (int i = cell.getStyles().getBlocks().size() - 1; i >= 0; i--) {
                cell.getStyles().getBlocks().get(i).getStyles().entrySet().stream()
                        .filter(entry -> !FORBIDDEN_CELL_TYPESETTING_STYLE_TYPES.contains(entry.getKey()))
                        .forEach(entry -> collectiveStyles.put(entry.getKey(), entry.getValue()));
            }
        }

        StyleModel styleModel = new DefaultStyleModel();
        styleModel.addBlock(new StyleBlock(
                new StyleSelectorBuilder().setTargetName("document").build(),
                collectiveStyles
        ));

        // Typeset cell
        double typeSetWidth = cellBounds.getSize().getWidth() - marginLeft - marginRight - paddingLeft - paddingRight;
        Page page = ctx.typesetThawTextFormat(
                cell.toString(),
                typeSetWidth,
                styleModel
        ).get(0);

        // Re-layout elements
        double typeSetHeight = 0;
        for (Element element : page.getElements()) {
            typeSetHeight = Math.max(typeSetHeight, element.getPosition().getY() + element.getSize().getHeight());

            AbstractElement abstractElement = (AbstractElement) element;
            abstractElement.setPosition(new Position(
                    abstractElement.getPosition().getX() + cellBounds.getPosition().getX() + ctx.getPositionContext().getX() + marginLeft + paddingLeft,
                    abstractElement.getPosition().getY() + cellBounds.getPosition().getY() + ctx.getPositionContext().getY() + marginTop + paddingTop
            ));
        }

        return new TypeSetCellInfo(
                page.getElements(),
                new Size(typeSetWidth, typeSetHeight),
                backgroundColor,
                new Color[]{
                        borderTopColor,
                        borderRightColor,
                        borderBottomColor,
                        borderLeftColor
                },
                new LineStyle[]{
                        borderTopStyle,
                        borderRightStyle,
                        borderBottomStyle,
                        borderLeftStyle
                },
                new Insets(borderTopWidth, borderRightWidth, borderBottomWidth, borderLeftWidth),
                new Insets(marginTop, marginRight, marginBottom, marginLeft),
                new Insets(paddingTop, paddingRight, paddingBottom, paddingLeft),
                borderRadius,
                verticalAlignment
        );
    }

    /**
     * Information about a already typeset cell.
     */
    private static class TypeSetCellInfo {

        /**
         * The content elements.
         */
        private final List<Element> elements;

        /**
         * Actual size of the typeset contents without margin or padding.
         */
        private final Size typeSetSize;

        /**
         * Background color of the cell.
         */
        private final Color backgroundColor;

        /**
         * Array of border colors in order: top, right, bottom, left.
         */
        private final Color[] borderColors;

        /**
         * Array of border styles in order: top, right, bottom, left.
         */
        private final LineStyle[] borderStyles;

        /**
         * Widths of the individual border sides.
         */
        private final Insets borderWidths;

        /**
         * Margin of the cell.
         */
        private final Insets margin;

        /**
         * Padding of the cell.
         */
        private final Insets padding;

        /**
         * Vertical alignment to apply to the element.
         */
        private final VerticalAlignment verticalAlignment;

        /**
         * Border radius to apply.
         */
        private final Insets borderRadius;

        public TypeSetCellInfo(List<Element> elements, Size typeSetSize, Color backgroundColor, Color[] borderColors, LineStyle[] borderStyles, Insets borderWidths, Insets margin, Insets padding, Insets borderRadius, VerticalAlignment verticalAlignment) {
            this.elements = elements;
            this.backgroundColor = backgroundColor;
            this.borderColors = borderColors;
            this.borderStyles = borderStyles;
            this.borderWidths = borderWidths;
            this.margin = margin;
            this.padding = padding;
            this.typeSetSize = typeSetSize;
            this.verticalAlignment = verticalAlignment;
            this.borderRadius = borderRadius;
        }

        public List<Element> getElements() {
            return elements;
        }

        public Size getTypeSetSize() {
            return typeSetSize;
        }

        public Color getBackgroundColor() {
            return backgroundColor;
        }

        public Color[] getBorderColors() {
            return borderColors;
        }

        public LineStyle[] getBorderStyles() {
            return borderStyles;
        }

        public Insets getBorderWidths() {
            return borderWidths;
        }

        public Insets getMargin() {
            return margin;
        }

        public Insets getPadding() {
            return padding;
        }

        public VerticalAlignment getVerticalAlignment() {
            return verticalAlignment;
        }

        public Insets getBorderRadius() {
            return borderRadius;
        }

    }

}
