package de.be.thaw.style.model;

import de.be.thaw.font.util.FontFamily;
import de.be.thaw.font.util.FontManager;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.BackgroundStyle;
import de.be.thaw.style.model.style.impl.ColorStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.ReferenceStyle;
import de.be.thaw.style.model.style.impl.SizeStyle;
import de.be.thaw.style.model.style.impl.TextStyle;
import de.be.thaw.util.HorizontalAlignment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Representation of the Thaw document style format model.
 */
public class StyleModel {

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
     * Map of blocks in the model.
     * Mapped by their name.
     */
    private final Map<String, StyleBlock> blocks;

    public StyleModel(Map<String, StyleBlock> blocks) {
        this.blocks = blocks;
    }

    /**
     * Get a block by its name.
     *
     * @param name to get block for
     * @return block
     */
    public Optional<StyleBlock> getBlock(String name) {
        if (name == null) {
            return Optional.empty();
        }

        return Optional.ofNullable(blocks.get(name.toLowerCase()));
    }

    /**
     * Add a style block to the model.
     *
     * @param name  of the block
     * @param block the block to add
     */
    public void addBlock(String name, StyleBlock block) {
        blocks.put(name.toLowerCase(), block);
    }

    /**
     * Get the default style model.
     *
     * @return default style model
     */
    public static StyleModel defaultModel() {
        StyleModel model = new StyleModel(new HashMap<>());

        // Document styles
        final Map<StyleType, Style> documentStyles = new HashMap<>();

        documentStyles.put(StyleType.SIZE, new SizeStyle(210.0, 297.0));
        documentStyles.put(StyleType.INSETS, new InsetsStyle(20.0, 25.0, 20.0, 25.0));
        documentStyles.put(StyleType.BACKGROUND, new BackgroundStyle(new ColorStyle(1.0, 1.0, 1.0, 1.0)));
        documentStyles.put(StyleType.TEXT, new TextStyle(
                10.0,
                null,
                HorizontalAlignment.LEFT,
                Boolean.TRUE,
                false,
                "Consolas",
                7.0,
                new ColorStyle(0.6, 0.6, 0.6, 1.0)
        ));
        documentStyles.put(StyleType.REFERENCE, new ReferenceStyle(
                new ColorStyle(
                        0.439,
                        0.503,
                        0.565,
                        1.0
                ),
                new ColorStyle(
                        0.439,
                        0.503,
                        0.565,
                        1.0
                )
        ));

        List<FontFamily> families = FontManager.getInstance().getFamiliesSupportingVariant(FontVariant.MONOSPACE);
        documentStyles.put(StyleType.FONT, new FontStyle(
                "Cambria",
                FontVariant.PLAIN,
                12.0,
                new ColorStyle(0.0, 0.0, 0.0, 1.0),
                families.isEmpty() ? null : families.get(0).getName(),
                KerningMode.NATIVE
        ));

        StyleBlock documentStyleBlock = new StyleBlock("DOCUMENT", documentStyles);
        model.addBlock(documentStyleBlock.getName(), documentStyleBlock);

        // Paragraph styles
        final Map<StyleType, Style> paragraphStyles = new HashMap<>();

        paragraphStyles.put(StyleType.INSETS, new InsetsStyle(0.0, 0.0, 2.0, 0.0));

        StyleBlock paragraphStyleBlock = new StyleBlock("PARAGRAPH", paragraphStyles);
        model.addBlock(paragraphStyleBlock.getName(), paragraphStyleBlock);

        // Headline styles
        for (int i = 1; i <= 6; i++) {
            String name = String.format("H%d", i);

            double fontSize = DEFAULT_HEADLINE_FONTSIZES[i - 1];
            double lineHeight = (fontSize - 10 * 0.5) * 1.2; // Just an estimation here!
            double insetsTop = 15;
            double insetsBottom = 3;

            Map<StyleType, Style> headlineStyles = new HashMap<>();
            headlineStyles.put(StyleType.TEXT, new TextStyle(0.0, lineHeight, HorizontalAlignment.LEFT, false, null, null, null, null));
            headlineStyles.put(StyleType.FONT, new FontStyle(null, FontVariant.BOLD, fontSize, null, null, null));
            headlineStyles.put(StyleType.INSETS, new InsetsStyle(insetsTop, 0.0, insetsBottom, 0.0));

            StyleBlock headlineStyleBlock = new StyleBlock(name, headlineStyles);
            model.addBlock(headlineStyleBlock.getName(), headlineStyleBlock);
        }

        // Code styles
        final Map<StyleType, Style> codeStyles = new HashMap<>();
        codeStyles.put(StyleType.FONT, new FontStyle(
                null,
                null,
                9.0,
                null,
                null,
                null
        ));
        codeStyles.put(StyleType.TEXT, new TextStyle(
                null,
                10.0,
                null,
                null,
                true,
                "Consolas",
                8.0,
                new ColorStyle(0.7, 0.7, 0.7, 1.0)
        ));
        StyleBlock codeStyleBlock = new StyleBlock("CODE", codeStyles);
        model.addBlock(codeStyleBlock.getName(), codeStyleBlock);

        return model;
    }

    /**
     * Merge this style model with the passed one.
     *
     * @param other to merge with
     * @return the merged style model
     */
    public StyleModel merge(StyleModel other) {
        if (other == null) {
            return this;
        }

        Map<String, StyleBlock> mergedBlocks = new HashMap<>();
        for (Map.Entry<String, StyleBlock> blockEntry : blocks.entrySet()) {
            Optional<StyleBlock> otherBlock = other.getBlock(blockEntry.getKey());
            if (otherBlock.isPresent()) {
                // Other style model does have the block -> merge the blocks
                mergedBlocks.put(blockEntry.getKey(), blockEntry.getValue().merge(otherBlock.get()));
            } else {
                // Does not have block -> keep it as is
                mergedBlocks.put(blockEntry.getKey(), blockEntry.getValue());
            }
        }

        // Add remaining blocks from the other style model (if any)
        for (Map.Entry<String, StyleBlock> blockEntry : other.blocks.entrySet()) {
            if (!mergedBlocks.containsKey(blockEntry.getKey())) {
                mergedBlocks.put(blockEntry.getKey(), blockEntry.getValue());
            }
        }

        return new StyleModel(mergedBlocks);
    }

}
