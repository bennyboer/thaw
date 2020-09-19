package de.be.thaw.style.parser.impl;

import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.parser.StyleFormatParser;
import de.be.thaw.style.parser.exception.StyleModelParseException;
import de.be.thaw.style.parser.lexer.StyleFormatLexerFactory;
import de.be.thaw.style.parser.lexer.exception.StyleFormatLexerException;
import de.be.thaw.style.parser.lexer.token.StyleFormatToken;
import org.jetbrains.annotations.Nullable;

import java.io.Reader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * The default style format parser.
 */
public class DefaultStyleFormatParser implements StyleFormatParser {

    @Override
    public StyleModel parse(Reader reader) throws StyleModelParseException {
        Map<List<StyleBlockSelector>, Map<String, String>> styleBlocks = parseStyleBlocksFromTokens(tokenize(reader));

        // TODO Convert style blocks to a style model

        return null; // TODO
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
     * Parse the passed tokens to style blocks.
     *
     * @param tokens to parse
     * @return style blocks
     * @throws StyleModelParseException in case style blocks could not be parsed properly
     */
    private Map<List<StyleBlockSelector>, Map<String, String>> parseStyleBlocksFromTokens(List<StyleFormatToken> tokens) throws StyleModelParseException {
        Map<List<StyleBlockSelector>, Map<String, String>> styleBlocks = new HashMap<>();

        // Temporary collections for parsing selectors and properties of a style block
        List<StyleBlockSelector> selectors = new ArrayList<>();
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
                        selectors.add(new StyleBlockSelector(
                                currentSelectorTargetName,
                                currentSelectorClassName,
                                currentSelectorPseudoClass,
                                currentSelectorPseudoClassSettings
                        ));

                        // Reset temporary variables
                        currentSelectorTargetName = null;
                        currentSelectorClassName = null;
                        currentSelectorPseudoClass = null;
                        currentSelectorPseudoClassSettings = null;

                        currentlyInBlock = true;
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

    /**
     * A selector in the style block (consisting of target name, class name and pseudo class (+ settings)).
     */
    private static class StyleBlockSelector {

        /**
         * Target name of the selector: 'document', 'page', 'paragraph', 'image', ...
         */
        private final String targetName;

        /**
         * Name of the class (if any).
         */
        @Nullable
        private final String className;

        /**
         * Name of the pseudo class (if any).
         */
        @Nullable
        private final String pseudoClassName;

        /**
         * Settings of the pseudo class (can be empty).
         * Can be null if there is no pseudo class name specified.
         */
        @Nullable
        private final List<String> pseudoClassSettings;

        public StyleBlockSelector(
                String targetName,
                @Nullable String className,
                @Nullable String pseudoClassName,
                @Nullable List<String> pseudoClassSettings
        ) {
            this.targetName = targetName;
            this.className = className;
            this.pseudoClassName = pseudoClassName;
            this.pseudoClassSettings = pseudoClassSettings;
        }

        public String getTargetName() {
            return targetName;
        }

        public Optional<String> getClassName() {
            return Optional.ofNullable(className);
        }

        public Optional<String> getPseudoClassName() {
            return Optional.ofNullable(pseudoClassName);
        }

        public Optional<List<String>> getPseudoClassSettings() {
            return Optional.ofNullable(pseudoClassSettings);
        }

        @Override
        public String toString() {
            StringBuilder sb = new StringBuilder();

            sb.append(getTargetName());

            getClassName().ifPresent(n -> {
                sb.append('.');
                sb.append(n);
            });

            getPseudoClassName().ifPresent(n -> {
                sb.append(':');
                sb.append(n);
            });

            getPseudoClassSettings().ifPresent(settings -> {
                if (!settings.isEmpty()) {
                    sb.append('(');
                    sb.append(String.join(", ", settings));
                    sb.append(')');
                }
            });

            return sb.toString();
        }
    }

}
