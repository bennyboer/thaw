package de.be.thaw.style.parser.impl;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.model.selector.StyleSelector;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.style.model.style.value.StyleValueCollection;
import de.be.thaw.style.parser.StyleFormatParser;
import de.be.thaw.style.parser.exception.StyleModelParseException;
import de.be.thaw.style.parser.lexer.StyleFormatLexerFactory;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatToken;
import de.be.thaw.style.parser.lexer.token.StyleFormatTokenType;
import de.be.thaw.style.parser.value.exception.StyleValueParseException;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * The default style format parser.
 */
public class DefaultStyleFormatParser implements StyleFormatParser {

    @Override
    public StyleModel parse(Reader reader) throws StyleModelParseException {
        return convertStyleBlocksToStyleModel(parseStyleBlocksFromTokens(tokenize(reader)));
    }

    /**
     * Tokenize the style format read from the passed reader.
     *
     * @param reader to read style format from
     * @return tokens
     * @throws StyleModelParseException in case the style format could not be tokenized
     */
    private List<StyleFormatToken> tokenize(Reader reader) throws StyleModelParseException {
        try {
            return StyleFormatLexerFactory.getInstance().getLexer().process(reader);
        } catch (StyleFormatLexerException e) {
            throw new StyleModelParseException(e);
        }
    }

    /**
     * Convert the passed style blocks to a style model.
     *
     * @param styleBlocks to convert
     * @return style model
     * @throws StyleModelParseException in case the style blocks could not be converted to a style model
     */
    private StyleModel convertStyleBlocksToStyleModel(Map<List<StyleSelector>, Map<String, String>> styleBlocks) throws StyleModelParseException {
        DefaultStyleModel model = new DefaultStyleModel();

        for (Map.Entry<List<StyleSelector>, Map<String, String>> entry : styleBlocks.entrySet()) {
            Map<StyleType, StyleValue> propertyMap = parseProperties(entry.getValue());

            for (StyleSelector selector : entry.getKey()) {
                model.addBlock(new StyleBlock(selector, propertyMap));
            }
        }

        return model;
    }

    /**
     * Parse the passed properties map.
     *
     * @param src map to parse
     * @return the parsed properties
     * @throws StyleModelParseException in case the properties could not be parsed
     */
    private Map<StyleType, StyleValue> parseProperties(Map<String, String> src) throws StyleModelParseException {
        Map<StyleType, StyleValue> result = new HashMap<>();

        for (Map.Entry<String, String> entry : src.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();

            StyleType type = StyleType.forKey(key);
            if (type == null) {
                throw new StyleModelParseException(String.format(
                        "Could not find style property '%s'",
                        key
                ));
            }

            StyleValue styleValue;
            try {
                styleValue = type.getParser().parse(value);
            } catch (StyleValueParseException e) {
                throw new StyleModelParseException(e);
            }

            if (styleValue instanceof StyleValueCollection) {
                // This is a special case where a single property wants to set multiple style properties at once.
                // For example the margin or padding property would return this.
                for (Map.Entry<StyleType, StyleValue> collectionEntry : ((StyleValueCollection) styleValue).getStyles().entrySet()) {
                    if (!result.containsKey(collectionEntry.getKey())) {
                        result.put(collectionEntry.getKey(), collectionEntry.getValue());
                    }
                }
            } else {
                result.put(type, styleValue);
            }
        }

        return result;
    }

    /**
     * Parse the passed tokens to style blocks.
     *
     * @param tokens to parse
     * @return style blocks
     * @throws StyleModelParseException in case style blocks could not be parsed properly
     */
    private Map<List<StyleSelector>, Map<String, String>> parseStyleBlocksFromTokens(List<StyleFormatToken> tokens) throws StyleModelParseException {
        Map<List<StyleSelector>, Map<String, String>> styleBlocks = new HashMap<>();

        // Temporary collections for parsing selectors and properties of a style block
        List<StyleSelector> selectors = new ArrayList<>();
        Map<String, String> propertyMap = new HashMap<>();

        // Temporary variables for parsing selectors
        String currentSelectorTargetName = null;
        String currentSelectorClassName = null;
        String currentSelectorPseudoClass = null;
        List<String> currentSelectorPseudoClassSettings = null;

        // Temporary variables for parsing properties
        String propertyKey = null;

        // Some flow-control temporary variables
        boolean currentlyInBlock = false;

        for (StyleFormatToken token : tokens) {
            switch (token.getType()) {
                case BLOCK_START_NAME -> currentSelectorTargetName = token.getValue().trim().toLowerCase();
                case BLOCK_START_CLASS_NAME -> currentSelectorClassName = token.getValue().trim().toLowerCase();
                case BLOCK_START_PSEUDO_CLASS_NAME -> currentSelectorPseudoClass = token.getValue().trim().toLowerCase();
                case BLOCK_START_PSEUDO_CLASS_SETTING -> {
                    if (currentSelectorPseudoClassSettings == null) {
                        currentSelectorPseudoClassSettings = new ArrayList<>();
                    }

                    currentSelectorPseudoClassSettings.add(token.getValue().trim());
                }
                case BLOCK_START_SEPARATOR, BLOCK_OPEN -> {
                    if (!currentlyInBlock) {
                        // Current selector is finished -> check if there is a target name first
                        if (currentSelectorTargetName == null || currentSelectorTargetName.isEmpty()) {
                            throw new StyleModelParseException(String.format(
                                    "Missing selector target name before line %d, position %d",
                                    token.getRange().getStart().getLine(),
                                    token.getRange().getStart().getPosition()
                            ));
                        }

                        // Add selector
                        selectors.add(new StyleSelectorBuilder()
                                .setTargetName(currentSelectorTargetName)
                                .setClassName(currentSelectorClassName)
                                .setPseudoClassName(currentSelectorPseudoClass)
                                .setPseudoClassSettings(currentSelectorPseudoClassSettings)
                                .build());

                        // Reset temporary variables
                        currentSelectorTargetName = null;
                        currentSelectorClassName = null;
                        currentSelectorPseudoClass = null;
                        currentSelectorPseudoClassSettings = null;

                        if (token.getType() == StyleFormatTokenType.BLOCK_OPEN) {
                            currentlyInBlock = true;
                        }
                    }
                }
                case PROPERTY -> propertyKey = token.getValue().trim().toLowerCase();
                case VALUE -> {
                    String propertyValue = token.getValue().trim();

                    // Add to current property map
                    propertyMap.put(propertyKey, propertyValue);
                }
                case BLOCK_CLOSE -> {
                    // Add new style block to the result map
                    styleBlocks.put(selectors, propertyMap);

                    // Reset selectors and property map collections
                    selectors = new ArrayList<>();
                    propertyMap = new HashMap<>();

                    currentlyInBlock = false;
                }
            }
        }

        return styleBlocks;
    }

}
