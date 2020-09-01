package de.be.thaw.style.parser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.font.util.KerningMode;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.BackgroundStyle;
import de.be.thaw.style.model.style.impl.ColorStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.HeaderFooterStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.ReferenceStyle;
import de.be.thaw.style.model.style.impl.SizeStyle;
import de.be.thaw.style.model.style.impl.TextStyle;
import de.be.thaw.util.HorizontalAlignment;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Deserializer for the style model JSON.
 */
public class StyleModelDeserializer extends StdDeserializer<StyleModel> {

    public StyleModelDeserializer() {
        this((Class<?>) null);
    }

    public StyleModelDeserializer(Class<?> vc) {
        super(vc);
    }

    public StyleModelDeserializer(JavaType valueType) {
        super(valueType);
    }

    public StyleModelDeserializer(StdDeserializer<?> src) {
        super(src);
    }

    @Override
    public StyleModel deserialize(JsonParser p, DeserializationContext ctx) throws IOException, JsonProcessingException {
        StyleModel styleModel = new StyleModel(new HashMap<>());

        JsonNode root = p.getCodec().readTree(p);

        for (Iterator<Map.Entry<String, JsonNode>> it = root.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> field = it.next();

            deserializeStyleBlock(field.getKey().toUpperCase(), field.getValue(), styleModel);
        }

        return styleModel;
    }

    /**
     * Deserialize the style block from the passed node.
     *
     * @param name       of the block
     * @param node       to deserialize from
     * @param styleModel the style model to add block to
     */
    private void deserializeStyleBlock(String name, JsonNode node, StyleModel styleModel) throws IOException {
        StyleBlock oldBlock = styleModel.getBlock(name).orElse(null);

        Map<StyleType, Style> styles = oldBlock == null ? new HashMap<>() : oldBlock.getStyles();
        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> field = it.next();

            StyleType type = StyleType.forKey(field.getKey().toLowerCase());
            if (type == null) {
                throw new IOException(String.format("No style type known for key '%s'", field.getKey()));
            }

            Style style = deserializeStyle(type, field.getValue(), oldBlock != null ? oldBlock.getStyles().get(type) : null);

            styles.put(type, style);
        }

        styleModel.addBlock(name, new StyleBlock(name, styles));
    }

    /**
     * Deserialize the passed style for the passed style type and JSON node.
     *
     * @param type     of the style
     * @param node     JSON node
     * @param oldStyle of the style model
     * @return style
     */
    private Style deserializeStyle(StyleType type, JsonNode node, Style oldStyle) {
        return switch (type) {
            case SIZE -> new SizeStyle(
                    node.has("width") ? node.get("width").asDouble(0) : null,
                    node.has("height") ? node.get("height").asDouble(0) : null
            ).merge(oldStyle);
            case INSETS -> new InsetsStyle(
                    node.has("top") ? node.get("top").asDouble(0) : null,
                    node.has("left") ? node.get("left").asDouble(0) : null,
                    node.has("bottom") ? node.get("bottom").asDouble(0) : null,
                    node.has("right") ? node.get("right").asDouble(0) : null
            ).merge(oldStyle);
            case COLOR -> new ColorStyle(
                    node.get("red").asDouble(1.0),
                    node.get("green").asDouble(1.0),
                    node.get("blue").asDouble(1.0),
                    node.has("alpha") ? node.get("alpha").asDouble(1.0) : 1.0
            ).merge(oldStyle);
            case BACKGROUND -> {
                ColorStyle color = node.has("color") ? (ColorStyle) deserializeStyle(StyleType.COLOR, node.get("color"), null) : null;
                yield new BackgroundStyle(color).merge(oldStyle);
            }
            case FONT -> {
                String variantStr = node.has("variant") ? node.get("variant").asText() : null;
                FontVariant variant = null;
                if (variantStr != null) {
                    for (FontVariant v : FontVariant.values()) {
                        String str = v.name().toLowerCase().replaceAll("_", " ");
                        if (str.equalsIgnoreCase(variantStr)) {
                            variant = v;
                            break;
                        }
                    }
                }

                ColorStyle color = node.has("color") ? (ColorStyle) deserializeStyle(StyleType.COLOR, node.get("color"), null) : null;

                KerningMode kerningMode = node.has("kerning") ? KerningMode.valueOf(node.get("kerning").asText("NATIVE").toUpperCase()) : null;

                yield new FontStyle(
                        node.has("family") ? node.get("family").asText(null) : null,
                        variant,
                        node.has("size") ? node.get("size").asDouble() : null,
                        color,
                        node.has("monoSpacedFontFamily") ? node.get("monoSpacedFontFamily").asText() : null,
                        kerningMode
                ).merge(oldStyle);
            }
            case TEXT -> new TextStyle(
                    node.has("firstLineIndent") ? node.get("firstLineIndent").asDouble() : null,
                    node.has("lineHeight") ? node.get("lineHeight").asDouble() : null,
                    node.has("alignment") ? HorizontalAlignment.valueOf(node.get("alignment").asText().toUpperCase()) : null,
                    node.has("justify") ? node.get("justify").asBoolean() : null,
                    node.has("showLineNumbers") ? node.get("showLineNumbers").asBoolean() : null,
                    node.has("lineNumberFontFamily") ? node.get("lineNumberFontFamily").asText() : null,
                    node.has("lineNumberFontSize") ? node.get("lineNumberFontSize").asDouble() : null,
                    node.has("lineNumberColor") ? (ColorStyle) deserializeStyle(StyleType.COLOR, node.get("lineNumberColor"), null) : null
            ).merge(oldStyle);
            case REFERENCE -> {
                ColorStyle internalColor = node.has("internalColor") ? (ColorStyle) deserializeStyle(StyleType.COLOR, node.get("internalColor"), null) : null;
                ColorStyle externalColor = node.has("externalColor") ? (ColorStyle) deserializeStyle(StyleType.COLOR, node.get("externalColor"), null) : null;

                yield new ReferenceStyle(
                        internalColor,
                        externalColor
                ).merge(oldStyle);
            }
            case HEADER_FOOTER -> new HeaderFooterStyle(
                    node.has("header") ? deserializeHeaderFooterSettings(node.get("header")) : null,
                    node.has("footer") ? deserializeHeaderFooterSettings(node.get("footer")) : null
            ).merge(oldStyle);
        };
    }

    /**
     * Deserialize the passed node to header footer settings.
     *
     * @param node to deserialize
     * @return the deserialized node
     */
    private HeaderFooterStyle.HeaderFooterSettings deserializeHeaderFooterSettings(JsonNode node) {
        String defaultSrc = node.has("default") ? node.get("default").asText() : null;

        List<HeaderFooterStyle.SpecialHeaderFooterSettings> specialSettings = null;
        if (node.has("special")) {
            specialSettings = new ArrayList<>();

            for (JsonNode specialNode : node.get("special")) {
                Integer startPage = specialNode.has("startPage") ? specialNode.get("startPage").asInt(1) : null;
                Integer endPage = specialNode.has("endPage") ? specialNode.get("endPage").asInt(Integer.MAX_VALUE) : null;
                String src = specialNode.get("src").asText();

                specialSettings.add(new HeaderFooterStyle.SpecialHeaderFooterSettings(startPage, endPage, src));
            }
        }

        return new HeaderFooterStyle.HeaderFooterSettings(defaultSrc, specialSettings);
    }

}
