package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.table.Table;
import de.be.thaw.table.cell.CellRange;
import de.be.thaw.table.cell.CellSpan;
import de.be.thaw.table.impl.DefaultTable;
import de.be.thaw.table.impl.exception.CouldNotMergeException;
import de.be.thaw.table.reader.TableReader;
import de.be.thaw.table.reader.csv.CSVTableReader;
import de.be.thaw.table.reader.exception.TableReadException;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.table.TableParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.table.ThawTableCell;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.unit.Unit;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

/**
 * Handler dealing with table thingies.
 */
public class TableHandler implements ThingyHandler {

    /**
     * The default separator to use for reading CSV tables.
     */
    private static final String DEFAULT_CSV_SEPARATOR = "|";

    /**
     * Importance of different pseudo selectors possible with tables.
     * Higher numbers mean higher importance.
     */
    private static final Map<String, Integer> PSEUDO_SELECTOR_IMPORTANCE = Map.ofEntries(
            Map.entry("cell", 999),
            Map.entry("cells", 900),
            Map.entry("row", 600),
            Map.entry("column", 500),
            Map.entry("rows", 400),
            Map.entry("columns", 300),
            Map.entry("even-rows", 200),
            Map.entry("even-columns", 100)
    );

    @Override
    public Set<String> getThingyNames() {
        return Set.of("TABLE");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        Paragraph currentParagraph = ctx.getCurrentParagraph();
        if (!(currentParagraph instanceof TextParagraph)) {
            throw new DocumentConversionException(String.format(
                    "Expected #TABLE# Thingy at %s to be in a text paragraph",
                    node.getTextPosition()
            ));
        }

        TextParagraph paragraph = (TextParagraph) currentParagraph;
        if (!paragraph.isEmpty()) {
            throw new DocumentConversionException(String.format(
                    "Expected #TABLE# Thingy at %s to be it's own paragraph and not being in-line with other text.",
                    node.getTextPosition()
            ));
        }

        String caption = node.getOptions().get("caption");
        String captionPrefix = node.getOptions().get("caption-prefix");

        Styles styles = documentNode.getStyles();
        Insets margin = new Insets(
                styles.resolve(StyleType.MARGIN_TOP).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.MARGIN_RIGHT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.MARGIN_BOTTOM).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.MARGIN_LEFT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0)
        );
        Insets padding = new Insets(
                styles.resolve(StyleType.PADDING_TOP).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.PADDING_RIGHT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.PADDING_BOTTOM).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0),
                styles.resolve(StyleType.PADDING_LEFT).map(v -> v.doubleValue(Unit.POINTS)).orElse(0.0)
        );

        double availableWidth = ctx.getLineWidth() - (margin.getLeft() + margin.getRight() + padding.getLeft() + padding.getRight());

        String tableContent = readTableContent(node, ctx);

        String format = node.getOptions().getOrDefault("format", "csv").toLowerCase().trim();
        TableReader<ThawTableCell> reader;
        if (format.equals("csv")) {
            // Fetch set separator or the default separator
            String csvSeparator = node.getOptions().getOrDefault("csv-separator", DEFAULT_CSV_SEPARATOR).trim();

            reader = new CSVTableReader<>(csvSeparator, 0, availableWidth);
        } else {
            throw new DocumentConversionException(String.format(
                    "Could not read table contents for #TABLE# thingy at %s since format '%s' is not supported. Try 'csv' as format.",
                    node.getTextPosition(),
                    format
            ));
        }

        // Read table
        Table<ThawTableCell> table;
        try {
            table = reader.read(new StringReader(tableContent), ThawTableCell::new);
        } catch (TableReadException e) {
            throw new DocumentConversionException(e);
        }

        applyCellStyles(table, node, ctx.getDocument().getStyleModel());

        ctx.setCurrentParagraph(new TableParagraph(
                table,
                availableWidth,
                documentNode,
                margin,
                padding,
                caption,
                captionPrefix
        ));
    }

    /**
     * Check if the passed style block needs to be applied on the current table.
     *
     * @param block     to apply
     * @param className of the table
     * @return whether the block applies
     */
    private boolean checkIfStyleBlockApplies(StyleBlock block, @Nullable String className) {
        if (block.getSelector().targetName().isPresent()) {
            if (block.getSelector().targetName().orElseThrow().equals("table-cell")) {
                boolean hasPseudoClass = block.getSelector().pseudoClassName().isPresent();
                boolean hasClassName = block.getSelector().className().isPresent();

                if (!hasClassName && !hasPseudoClass) {
                    return true; // Applies to all tables
                } else if (hasClassName && !hasPseudoClass) {
                    if (className == null) {
                        return false;
                    } else {
                        return block.getSelector().className().orElseThrow().equals(className);
                    }
                } else if (!hasClassName && hasPseudoClass) {
                    return true; // Applies to all tables
                } else {
                    if (className == null) {
                        return false;
                    } else {
                        return block.getSelector().className().orElseThrow().equals(className);
                    }
                }
            }
        }

        return false;
    }

    /**
     * Get a priority for the passed style block.
     *
     * @param block to get priority for
     * @return priority
     */
    private int getPriorityForStyleBlock(StyleBlock block) {
        String className = block.getSelector().className().orElse(null);
        String pseudoClassName = block.getSelector().pseudoClassName().orElse(null);

        if (className == null && pseudoClassName == null) {
            return -2; // Lowest priority
        } else if (className == null && pseudoClassName != null) {
            return PSEUDO_SELECTOR_IMPORTANCE.get(pseudoClassName);
        } else if (className != null && pseudoClassName == null) {
            return -1;
        } else {
            return PSEUDO_SELECTOR_IMPORTANCE.get(pseudoClassName) * 100;
        }
    }

    /**
     * Apply cell styles set in the given style model to the table cells.
     *
     * @param table      to apply styles to cells of
     * @param node       of the table thingy
     * @param styleModel the style model to apply styles set in
     */
    private void applyCellStyles(Table<ThawTableCell> table, ThingyNode node, StyleModel styleModel) throws DocumentConversionException {
        String className = node.getOptions().get("class");

        if (table instanceof DefaultTable) {
            styleModel.getBlocks().stream()
                    // Filter to only contain relevant style blocks for the current table thingy
                    .filter(block -> checkIfStyleBlockApplies(block, className))
                    // Sort blocks by their importance (First index should be the least important afterwards).
                    .sorted(Comparator.comparingInt(this::getPriorityForStyleBlock))
                    // Apply styles in the order of the table blocks (least important first)
                    .forEach(block -> applyStyleBlockToTableCells((DefaultTable<ThawTableCell>) table, block));
        } else {
            throw new DocumentConversionException("Could not apply styles to the table");
        }
    }

    /**
     * Apply the passed style block to the given tables cells.
     *
     * @param table to apply styles to cells of
     * @param block to process styles in
     */
    private void applyStyleBlockToTableCells(DefaultTable<ThawTableCell> table, StyleBlock block) {
        boolean hasPseudoClass = block.getSelector().pseudoClassName().isPresent();

        if (hasPseudoClass) {
            List<Integer> settings = new ArrayList<>();
            List<String> rawSettings = block.getSelector().pseudoClassSettings().orElse(Collections.emptyList());
            for (int i = 0; i < rawSettings.size(); i++) {
                String rawSetting = rawSettings.get(i);

                if (rawSetting.equals("*")) {
                    String pseudoClassName = block.getSelector().pseudoClassName().orElseThrow();

                    // Means last row or column depending on pseudo class name
                    if (pseudoClassName.equals("row") || pseudoClassName.equals("rows")) {
                        settings.add(table.getRows());
                    } else if (pseudoClassName.equals("column") || pseudoClassName.equals("columns")) {
                        settings.add(table.getColumns());
                    } else if (pseudoClassName.equals("cell")) {
                        if (i == 0) {
                            settings.add(table.getRows());
                        } else {
                            settings.add(table.getColumns());
                        }
                    } else if (pseudoClassName.equals("cells")) {
                        if (i == 0 || i == 1) {
                            settings.add(table.getRows());
                        } else {
                            settings.add(table.getColumns());
                        }
                    } else {
                        throw new RuntimeException(String.format(
                                "* modifier of selector '%s' can only be applied to :row, :rows, :column or :columns pseudo classes",
                                block.getSelector().toString()
                        ));
                    }
                } else {
                    settings.add(Integer.parseInt(rawSetting));
                }
            }

            // Build cell span to apply styles to
            boolean allowMerge = false;
            boolean setRowSize = false;
            boolean setColumnSize = false;
            CellSpan span = switch (block.getSelector().pseudoClassName().orElseThrow()) {
                case "cell" -> new CellSpan(
                        settings.get(0),
                        settings.get(1)
                );
                case "cells" -> {
                    allowMerge = true;

                    yield new CellSpan(
                            new CellRange(settings.get(0), settings.get(1)),
                            new CellRange(settings.get(2), settings.get(3))
                    );
                }
                case "row" -> {
                    setRowSize = true;

                    yield new CellSpan(
                            new CellRange(settings.get(0)),
                            new CellRange(1, table.getColumns())
                    );
                }
                case "column" -> {
                    setColumnSize = true;

                    yield new CellSpan(
                            new CellRange(1, table.getRows()),
                            new CellRange(settings.get(0))
                    );
                }
                case "rows" -> {
                    setRowSize = true;

                    yield new CellSpan(
                            new CellRange(settings.get(0), settings.get(1)),
                            new CellRange(1, table.getColumns())
                    );
                }
                case "columns" -> {
                    setColumnSize = true;

                    yield new CellSpan(
                            new CellRange(1, table.getRows()),
                            new CellRange(settings.get(0), settings.get(1))
                    );
                }
                default -> new CellSpan(
                        new CellRange(1, table.getRows()),
                        new CellRange(1, table.getColumns())
                );
            };

            applyStyleBlockToCellsInSpan(span, table, block, allowMerge, setRowSize, setColumnSize);
        } else {
            applyStyleBlockToCellsInSpan(new CellSpan(
                    new CellRange(1, table.getRows()),
                    new CellRange(1, table.getColumns())
            ), table, block, false, false, false);
        }
    }

    /**
     * Apply the passed style block to the given span in the table.
     *
     * @param span          to apply the styles to
     * @param table         to apply the styles to
     * @param block         holding the styles to apply
     * @param allowMerge    whether we allow cell merging
     * @param setRowSize    whether we are allowed to set the row sizes
     * @param setColumnSize whether we are
     */
    private void applyStyleBlockToCellsInSpan(
            CellSpan span,
            DefaultTable<ThawTableCell> table,
            StyleBlock block,
            boolean allowMerge,
            boolean setRowSize,
            boolean setColumnSize
    ) {
        if (setRowSize) {
            Optional.ofNullable(block.getStyles().get(StyleType.HEIGHT))
                    .map(value -> value.doubleValue(Unit.POINTS))
                    .ifPresent(height -> {
                        for (int row = span.getRowRange().getStart(); row <= span.getRowRange().getEnd(); row++) {
                            table.setRowSize(row, height);
                        }
                    });
        }

        if (setColumnSize) {
            Optional.ofNullable(block.getStyles().get(StyleType.WIDTH))
                    .map(value -> value.doubleValue(Unit.POINTS))
                    .ifPresent(width -> {
                        for (int column = span.getColumnRange().getStart(); column <= span.getColumnRange().getEnd(); column++) {
                            table.setColumnSize(column, width);
                        }
                    });
        }

        if (allowMerge && Optional.ofNullable(block.getStyles().get(StyleType.MERGE))
                .map(StyleValue::booleanValue)
                .orElse(false)) {
            try {
                table.merge(span);
            } catch (CouldNotMergeException e) {
                throw new RuntimeException(e);
            }
        }

        String pseudoClassName = block.getSelector().pseudoClassName().orElse("");
        boolean evenRows = pseudoClassName.equals("even-rows");
        boolean evenColumns = pseudoClassName.equals("even-columns");

        for (int row = span.getRowRange().getStart(); row <= span.getRowRange().getEnd(); row++) {
            boolean isEvenRow = row % 2 == 0;
            if (evenRows && !isEvenRow) {
                continue;
            }

            for (int column = span.getColumnRange().getStart(); column <= span.getColumnRange().getEnd(); column++) {
                boolean isEvenColumn = column % 2 == 0;
                if (evenColumns && !isEvenColumn) {
                    continue;
                }

                // Set individual cell styles
                ThawTableCell cell = table.getCell(row, column).orElseThrow();

                List<StyleBlock> newBlocks = new ArrayList<>();
                newBlocks.add(block);
                if (cell.getStyles() != null) {
                    newBlocks.addAll(cell.getStyles().getBlocks());
                }

                cell.setStyles(new Styles(newBlocks));
            }
        }
    }

    /**
     * Read the table content as string.
     *
     * @param node to read content from
     * @param ctx  the conversion context
     * @return the read table content
     */
    private String readTableContent(ThingyNode node, ConversionContext ctx) throws DocumentConversionException {
        String tableSrc = node.getOptions().get("src");
        if (tableSrc == null) {
            // We expect then to have the content in the first argument
            tableSrc = node.getArguments().iterator().next();
            if (tableSrc == null) {
                throw new DocumentConversionException(String.format(
                        "#TABLE# Thingy at %s is expected to either specify a source file using the 'src' option or the contents of the table in the first argument",
                        node.getTextPosition()
                ));
            }
        } else {
            // Load table source from file
            File file = new File(ctx.getConfig().getWorkingDirectory(), tableSrc);
            try {
                tableSrc = Files.readString(file.toPath(), ctx.getDocument().getInfo().getEncoding());
            } catch (IOException e) {
                throw new DocumentConversionException(String.format(
                        "Could not read specified table source file at '%s' from #TABLE# Thingy at %s",
                        file.getAbsolutePath(),
                        node.getTextPosition()
                ));
            }
        }

        return tableSrc;
    }

}
