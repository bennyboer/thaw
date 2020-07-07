package de.be.thaw.style.parser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.be.thaw.font.util.FontVariant;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.BackgroundStyle;
import de.be.thaw.style.model.style.impl.ColorStyle;
import de.be.thaw.style.model.style.impl.FirstLineIndentStyle;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.LineHeightStyle;
import de.be.thaw.style.model.style.impl.SizeStyle;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
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
        JsonNode root = p.getCodec().readTree(p);

        Map<String, StyleBlock> blocks = new HashMap<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = root.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> field = it.next();

            blocks.put(field.getKey().toLowerCase(), deserializeStyleBlock(field.getKey().toUpperCase(), field.getValue(), p, ctx));
        }

        return new StyleModel(blocks);
    }

    /**
     * Deserialize the style block from the passed node.
     *
     * @param name of the block
     * @param node to deserialize from
     * @param p    the JSON parser
     * @param ctx  the deserialization context
     * @return the parsed style block
     */
    private StyleBlock deserializeStyleBlock(String name, JsonNode node, JsonParser p, DeserializationContext ctx) throws IOException {
        Map<StyleType, Style> styles = new HashMap<>();
        for (Iterator<Map.Entry<String, JsonNode>> it = node.fields(); it.hasNext(); ) {
            Map.Entry<String, JsonNode> field = it.next();

            StyleType type = StyleType.forKey(field.getKey().toLowerCase());
            if (type == null) {
                throw new IOException(String.format("No style type known for key '%s'", field.getKey()));
            }

            Style style = deserializeStyle(type, field.getValue());

            styles.put(type, style);
        }

        return new StyleBlock(name, styles);
    }

    /**
     * Deserialize the passed style for the passed style type and JSON node.
     *
     * @param type of the style
     * @param node JSON node
     * @return style
     */
    private Style deserializeStyle(StyleType type, JsonNode node) {
        return switch (type) {
            case SIZE -> new SizeStyle(
                    node.get("width").asDouble(0),
                    node.get("height").asDouble(0)
            );
            case INSETS -> new InsetsStyle(
                    node.has("top") ? node.get("top").asDouble(0) : 0,
                    node.has("left") ? node.get("left").asDouble(0) : 0,
                    node.has("bottom") ? node.get("bottom").asDouble(0) : 0,
                    node.has("right") ? node.get("right").asDouble(0) : 0
            );
            case COLOR -> new ColorStyle(
                    node.get("red").asDouble(1.0),
                    node.get("green").asDouble(1.0),
                    node.get("blue").asDouble(1.0),
                    node.has("alpha") ? node.get("alpha").asDouble(1.0) : 1.0
            );
            case BACKGROUND -> {
                ColorStyle color = node.has("color") ? (ColorStyle) deserializeStyle(StyleType.COLOR, node.get("color")) : null;
                yield new BackgroundStyle(color);
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

                ColorStyle color = node.has("color") ? (ColorStyle) deserializeStyle(StyleType.COLOR, node.get("color")) : null;

                yield new FontStyle(
                        node.has("family") ? node.get("family").asText(null) : null,
                        variant,
                        node.has("size") ? node.get("size").asDouble() : null,
                        color
                );
            }
            case FIRST_LINE_INDENT -> new FirstLineIndentStyle(node.asDouble());
            case LINE_HEIGHT -> new LineHeightStyle(node.asDouble());
        };
    }

}
