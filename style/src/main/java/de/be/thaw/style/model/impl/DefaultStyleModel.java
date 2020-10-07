package de.be.thaw.style.model.impl;

import de.be.thaw.font.util.FontFamily;
import de.be.thaw.font.util.FontManager;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.selector.StyleSelector;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.util.FillStyle;
import de.be.thaw.style.model.style.util.list.ListStyleType;
import de.be.thaw.style.model.style.value.BooleanStyleValue;
import de.be.thaw.style.model.style.value.ColorStyleValue;
import de.be.thaw.style.model.style.value.DoubleStyleValue;
import de.be.thaw.style.model.style.value.FillStyleValue;
import de.be.thaw.style.model.style.value.FontFamilyStyleValue;
import de.be.thaw.style.model.style.value.FontVariantStyleValue;
import de.be.thaw.style.model.style.value.HorizontalAlignmentStyleValue;
import de.be.thaw.style.model.style.value.IntStyleValue;
import de.be.thaw.style.model.style.value.KerningModeStyleValue;
import de.be.thaw.style.model.style.value.ListStyleTypeStyleValue;
import de.be.thaw.style.model.style.value.StringStyleValue;
import de.be.thaw.style.model.style.value.VerticalAlignmentStyleValue;
import de.be.thaw.util.HorizontalAlignment;
import de.be.thaw.util.VerticalAlignment;
import de.be.thaw.util.color.Color;
import de.be.thaw.util.unit.Unit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Representation of the Thaw document style format model.
 */
public class DefaultStyleModel implements StyleModel {

    /**
     * Default headline font sizes.
     */
    private static final int[] DEFAULT_HEADLINE_FONTSIZES = new int[]{
            26,
            22,
            18,
            16,
            14,
            13
    };

    /**
     * Style blocks in the model.
     */
    private final List<StyleBlock> blocks;

    /**
     * Lookup of style blocks by their selector.
     */
    private final Map<String, StyleBlock> blockLookup = new HashMap<>();

    public DefaultStyleModel() {
        this.blocks = new ArrayList<>();
    }

    public DefaultStyleModel(List<StyleBlock> blocks) {
        this.blocks = blocks;

        for (StyleBlock block : blocks) {
            blockLookup.put(block.getSelector().toString(), block);
        }
    }

    /**
     * Get the default style model.
     *
     * @return default style model
     */
    public static StyleModel defaultModel() {
        DefaultStyleModel model = new DefaultStyleModel();

        List<FontFamily> monospaceFamilies = FontManager.getInstance().getFamiliesSupportingVariant(FontVariant.MONOSPACE);
        List<FontFamily> plainFamilies = FontManager.getInstance().getFamiliesSupportingVariant(FontVariant.PLAIN);

        // Document style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("document")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.WIDTH, new IntStyleValue(210, Unit.MILLIMETER)),
                        Map.entry(StyleType.HEIGHT, new IntStyleValue(297, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(25.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(25.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(20.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(20.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_TOP, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_BOTTOM, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.BACKGROUND_COLOR, new ColorStyleValue(new Color(1.0, 1.0, 1.0, 0.0))), // White background color
                        Map.entry(StyleType.FIRST_LINE_INDENT, new DoubleStyleValue(0.6, Unit.CENTIMETER)),
                        Map.entry(StyleType.TEXT_ALIGN, new HorizontalAlignmentStyleValue(HorizontalAlignment.LEFT)),
                        Map.entry(StyleType.LINE_HEIGHT, new DoubleStyleValue(1.2, Unit.UNITARY)),
                        Map.entry(StyleType.TEXT_JUSTIFY, new BooleanStyleValue(true)),
                        Map.entry(StyleType.SHOW_LINE_NUMBERS, new BooleanStyleValue(false)),
                        Map.entry(StyleType.LINE_NUMBER_FONT_FAMILY, new FontFamilyStyleValue(monospaceFamilies.isEmpty() ? "" : monospaceFamilies.get(0).getName())),
                        Map.entry(StyleType.LINE_NUMBER_FONT_SIZE, new DoubleStyleValue(7.0, Unit.POINTS)),
                        Map.entry(StyleType.LINE_NUMBER_COLOR, new ColorStyleValue(new Color(0.6, 0.6, 0.6))),
                        Map.entry(StyleType.FONT_FAMILY, new FontFamilyStyleValue(plainFamilies.isEmpty() ? "" : plainFamilies.get(0).getName())),
                        Map.entry(StyleType.FONT_VARIANT, new FontVariantStyleValue(FontVariant.PLAIN)),
                        Map.entry(StyleType.FONT_SIZE, new IntStyleValue(12, Unit.POINTS)),
                        Map.entry(StyleType.FONT_KERNING, new KerningModeStyleValue(KerningMode.NATIVE)),
                        Map.entry(StyleType.COLOR, new ColorStyleValue(new Color(0.0, 0.0, 0.0))),
                        Map.entry(StyleType.INTERNAL_LINK_COLOR, new ColorStyleValue(new Color(0.439, 0.503, 0.565))),
                        Map.entry(StyleType.EXTERNAL_LINK_COLOR, new ColorStyleValue(new Color(0.439, 0.503, 0.565))),
                        Map.entry(StyleType.HYPHENATION, new BooleanStyleValue(true))
                )
        ));

        // Paragraph style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("paragraph")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.MARGIN_BOTTOM, new IntStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(0.0, Unit.MILLIMETER))
                )
        ));

        // General headline 'h' style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("h")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.FIRST_LINE_INDENT, new IntStyleValue(0, Unit.CENTIMETER)),
                        Map.entry(StyleType.LINE_HEIGHT, new DoubleStyleValue(1.4, Unit.UNITARY)),
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(15, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(3, Unit.MILLIMETER)),
                        Map.entry(StyleType.FONT_VARIANT, new FontVariantStyleValue(FontVariant.BOLD))
                )
        ));

        // Headline level-specific style blocks
        for (int i = 1; i <= 6; i++) {
            model.addBlock(new StyleBlock(
                    new StyleSelectorBuilder()
                            .setTargetName(String.format("h%d", i))
                            .build(),
                    Map.ofEntries(
                            Map.entry(StyleType.FONT_SIZE, new DoubleStyleValue(DEFAULT_HEADLINE_FONTSIZES[i - 1], Unit.POINTS))
                    )
            ));
        }

        // Code style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("code")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.FONT_FAMILY, new FontFamilyStyleValue(monospaceFamilies.isEmpty() ? "" : monospaceFamilies.get(0).getName())),
                        Map.entry(StyleType.LINE_HEIGHT, new DoubleStyleValue(1.3, Unit.UNITARY)),
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_BOTTOM, new DoubleStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_TOP, new DoubleStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_LEFT, new DoubleStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_RIGHT, new DoubleStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.FONT_VARIANT, new FontVariantStyleValue(FontVariant.MONOSPACE)),
                        Map.entry(StyleType.FONT_SIZE, new IntStyleValue(9, Unit.POINTS)),
                        Map.entry(StyleType.FONT_KERNING, new KerningModeStyleValue(KerningMode.NATIVE)),
                        Map.entry(StyleType.SHOW_LINE_NUMBERS, new BooleanStyleValue(true)),
                        Map.entry(StyleType.LINE_NUMBER_FONT_FAMILY, new StringStyleValue(monospaceFamilies.isEmpty() ? "" : monospaceFamilies.get(0).getName())),
                        Map.entry(StyleType.LINE_NUMBER_FONT_SIZE, new IntStyleValue(8, Unit.POINTS)),
                        Map.entry(StyleType.LINE_NUMBER_COLOR, new ColorStyleValue(new Color(0.7, 0.7, 0.7)))
                )
        ));

        // Image style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("image")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_BOTTOM, new DoubleStyleValue(3, Unit.MILLIMETER))
                )
        ));

        // Table of contents style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("toc")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(2, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_BOTTOM, new DoubleStyleValue(3, Unit.MILLIMETER)),
                        Map.entry(StyleType.FIRST_LINE_INDENT, new DoubleStyleValue(0.0, Unit.MILLIMETER))
                )
        ));

        // Table of contents entry style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("toc-entry")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_BOTTOM, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.TEXT_JUSTIFY, new BooleanStyleValue(false)),
                        Map.entry(StyleType.FILL, new FillStyleValue(FillStyle.DOTTED)),
                        Map.entry(StyleType.FILL_SIZE, new DoubleStyleValue(0.25, Unit.MILLIMETER))
                )
        ));

        // Add indent per level (6 levels are predefined, H1 to H6)
        for (int i = 1; i <= 6; i++) {
            model.addBlock(new StyleBlock(
                    new StyleSelectorBuilder()
                            .setTargetName(String.format("toc-entry:level(%d)", i))
                            .build(),
                    Map.ofEntries(
                            Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue((double) 5 * (i - 1), Unit.MILLIMETER))
                    )
            ));
        }

        // Foot note style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("footnote")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.FIRST_LINE_INDENT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.FONT_SIZE, new DoubleStyleValue(10.0, Unit.POINTS)),
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(2.0, Unit.MILLIMETER))
                )
        ));

        // Add general foot notes paragraph style block
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("footnotes")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(2.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_TOP, new DoubleStyleValue(3.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.FOOT_NOTE_LINE_COLOR, new ColorStyleValue(new Color(0.0, 0.0, 0.0))),
                        Map.entry(StyleType.FOOT_NOTE_LINE_SIZE, new DoubleStyleValue(0.25, Unit.MILLIMETER))
                )
        ));

        // Add enumeration style blocks per level for unordered enumerations
        for (int i = 1; i <= 9; i++) {
            int rest = i % 4;

            ListStyleType listStyleType = ListStyleType.MINUS;
            if (rest == 1) {
                listStyleType = ListStyleType.BULLET;
            } else if (rest == 2) {
                listStyleType = ListStyleType.SQUARE;
            } else if (rest == 3) {
                listStyleType = ListStyleType.CIRCLE;
            }

            model.addBlock(new StyleBlock(
                    new StyleSelectorBuilder()
                            .setTargetName(String.format("enumeration:level(%d)", i))
                            .build(),
                    Map.ofEntries(
                            Map.entry(StyleType.LIST_STYLE_TYPE, new ListStyleTypeStyleValue(listStyleType))
                    )
            ));
        }

        // Add enumeration style blocks per level for ordered enumerations
        for (int i = 1; i <= 9; i++) {
            int rest = i % 5;

            ListStyleType listStyleType = ListStyleType.UPPER_ROMAN;
            if (rest == 1) {
                listStyleType = ListStyleType.DECIMAL;
            } else if (rest == 2) {
                listStyleType = ListStyleType.LOWER_LATIN;
            } else if (rest == 3) {
                listStyleType = ListStyleType.LOWER_ROMAN;
            } else if (rest == 4) {
                listStyleType = ListStyleType.UPPER_LATIN;
            }

            model.addBlock(new StyleBlock(
                    new StyleSelectorBuilder()
                            .setTargetName(String.format("enumeration.ordered:level(%d)", i))
                            .build(),
                    Map.ofEntries(
                            Map.entry(StyleType.LIST_STYLE_TYPE, new ListStyleTypeStyleValue(listStyleType))
                    )
            ));
        }

        // Add table and table cell styles
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("table")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(2.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(2.0, Unit.MILLIMETER))
                )
        ));
        model.addBlock(new StyleBlock(
                new StyleSelectorBuilder()
                        .setTargetName("table-cell")
                        .build(),
                Map.ofEntries(
                        Map.entry(StyleType.FIRST_LINE_INDENT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.LINE_HEIGHT, new DoubleStyleValue(1.0, Unit.UNITARY)),
                        Map.entry(StyleType.TEXT_JUSTIFY, new BooleanStyleValue(false)),
                        Map.entry(StyleType.TEXT_ALIGN, new HorizontalAlignmentStyleValue(HorizontalAlignment.CENTER)),
                        Map.entry(StyleType.VERTICAL_ALIGN, new VerticalAlignmentStyleValue(VerticalAlignment.CENTER)),
                        Map.entry(StyleType.MARGIN_LEFT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_RIGHT, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_TOP, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.MARGIN_BOTTOM, new DoubleStyleValue(0.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_TOP, new DoubleStyleValue(1.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_BOTTOM, new DoubleStyleValue(1.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_LEFT, new DoubleStyleValue(1.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.PADDING_RIGHT, new DoubleStyleValue(1.0, Unit.MILLIMETER)),
                        Map.entry(StyleType.BACKGROUND_COLOR, new ColorStyleValue(new Color(1.0, 1.0, 1.0))),
                        Map.entry(StyleType.BORDER_TOP_COLOR, new ColorStyleValue(new Color(0.7, 0.7, 0.7))),
                        Map.entry(StyleType.BORDER_TOP_WIDTH, new DoubleStyleValue(0.25, Unit.MILLIMETER)),
                        Map.entry(StyleType.BORDER_TOP_STYLE, new FillStyleValue(FillStyle.SOLID)),
                        Map.entry(StyleType.BORDER_LEFT_COLOR, new ColorStyleValue(new Color(0.7, 0.7, 0.7))),
                        Map.entry(StyleType.BORDER_LEFT_WIDTH, new DoubleStyleValue(0.25, Unit.MILLIMETER)),
                        Map.entry(StyleType.BORDER_LEFT_STYLE, new FillStyleValue(FillStyle.SOLID)),
                        Map.entry(StyleType.BORDER_BOTTOM_COLOR, new ColorStyleValue(new Color(0.7, 0.7, 0.7))),
                        Map.entry(StyleType.BORDER_BOTTOM_WIDTH, new DoubleStyleValue(0.25, Unit.MILLIMETER)),
                        Map.entry(StyleType.BORDER_BOTTOM_STYLE, new FillStyleValue(FillStyle.SOLID)),
                        Map.entry(StyleType.BORDER_RIGHT_COLOR, new ColorStyleValue(new Color(0.7, 0.7, 0.7))),
                        Map.entry(StyleType.BORDER_RIGHT_WIDTH, new DoubleStyleValue(0.25, Unit.MILLIMETER)),
                        Map.entry(StyleType.BORDER_RIGHT_STYLE, new FillStyleValue(FillStyle.SOLID))
                )
        ));

        return model;
    }

    @Override
    public List<StyleBlock> getBlocks() {
        return blocks;
    }

    @Override
    public void addBlock(StyleBlock block) {
        // Check if there is already a style block with the same selector -> if yes: merge them!
        Optional<StyleBlock> oldStyleBlock = getBlock(block.getSelector());
        if (oldStyleBlock.isPresent()) {
            blocks.remove(oldStyleBlock.get());
            block = block.merge(oldStyleBlock.get()); // Merge blocks
        }

        blocks.add(block);
        blockLookup.put(block.getSelector().toString(), block);
    }

    @Override
    public Optional<StyleBlock> getBlock(StyleSelector selector) {
        return Optional.ofNullable(blockLookup.get(selector.toString()));
    }

    @Override
    public StyleModel merge(StyleModel other) {
        if (other == null) {
            return this;
        }

        DefaultStyleModel mergedModel = new DefaultStyleModel(new ArrayList<>(other.getBlocks()));

        for (StyleBlock block : getBlocks()) {
            mergedModel.addBlock(block);
        }

        return mergedModel;
    }

    @Override
    public Styles select(StyleSelector... selectors) {
        List<StyleBlock> styleBlocks = new ArrayList<>();

        // Add style blocks for selectors in descending priority (latter added are less important)
        for (StyleSelector selector : selectors) {
            boolean hasClassName = selector.className().isPresent();
            boolean hasPseudoClassName = selector.pseudoClassName().isPresent();

            if (hasClassName && hasPseudoClassName) {
                getBlock(new StyleSelectorBuilder()
                        .setTargetName(selector.targetName().orElseThrow())
                        .setClassName(selector.className().orElseThrow())
                        .setPseudoClassName(selector.pseudoClassName().orElseThrow())
                        .setPseudoClassSettings(selector.pseudoClassSettings().orElse(null))
                        .build()).ifPresent(styleBlocks::add);
            }

            if (hasClassName) {
                getBlock(new StyleSelectorBuilder()
                        .setTargetName(selector.targetName().orElseThrow())
                        .setClassName(selector.className().orElseThrow())
                        .build()).ifPresent(styleBlocks::add);
            }

            if (hasPseudoClassName) {
                getBlock(new StyleSelectorBuilder()
                        .setTargetName(selector.targetName().orElseThrow())
                        .setPseudoClassName(selector.pseudoClassName().orElseThrow())
                        .setPseudoClassSettings(selector.pseudoClassSettings().orElse(null))
                        .build()).ifPresent(styleBlocks::add);
            }

            if (selector.targetName().isPresent()) {
                getBlock(new StyleSelectorBuilder()
                        .setTargetName(selector.targetName().orElseThrow())
                        .build()).ifPresent(styleBlocks::add);
            }
        }

        // Add document styles as the last block anyway (contains defaults for everything)
        styleBlocks.add(getBlock(new StyleSelectorBuilder().setTargetName("document").build()).orElseThrow());

        return new Styles(styleBlocks);
    }

}
