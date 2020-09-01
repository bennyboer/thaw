package de.be.thaw.typeset.knuthplass;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.util.PageRange;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.converter.KnuthPlassConverter;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.handler.impl.CodeParagraphHandler;
import de.be.thaw.typeset.knuthplass.paragraph.handler.impl.ImageParagraphHandler;
import de.be.thaw.typeset.knuthplass.paragraph.handler.impl.MathParagraphHandler;
import de.be.thaw.typeset.knuthplass.paragraph.handler.impl.TableOfContentsItemParagraphHandler;
import de.be.thaw.typeset.knuthplass.paragraph.handler.impl.TextParagraphHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.code.CodeParagraph;
import de.be.thaw.typeset.page.Page;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of the Knuth-Plass line breaking algorithm.
 */
public class KnuthPlassTypeSetter implements TypeSetter {

    /**
     * Mapping of paragraph types to their typesetting handlers.
     */
    private static final Map<ParagraphType, ParagraphTypesetHandler> PARAGRAPH_HANDLER_MAP = new EnumMap<>(ParagraphType.class);

    static {
        initParagraphTypesetHandler(new TextParagraphHandler());
        initParagraphTypesetHandler(new ImageParagraphHandler());
        initParagraphTypesetHandler(new TableOfContentsItemParagraphHandler());
        initParagraphTypesetHandler(new MathParagraphHandler());
        initParagraphTypesetHandler(new CodeParagraphHandler());
    }

    /**
     * Configuration of the typesetting.
     */
    private final KnuthPlassTypeSettingConfig config;

    public KnuthPlassTypeSetter(KnuthPlassTypeSettingConfig config) {
        this.config = config;
    }

    /**
     * Initialize the passed paragraph typesetting handler.
     *
     * @param handler to initialize
     */
    private static void initParagraphTypesetHandler(ParagraphTypesetHandler handler) {
        PARAGRAPH_HANDLER_MAP.put(handler.supportedType(), handler);
    }

    /**
     * Get a handler by the passed type.
     *
     * @param type to get handler for
     * @return the handler
     */
    private static Optional<ParagraphTypesetHandler> getHandler(ParagraphType type) {
        return Optional.ofNullable(PARAGRAPH_HANDLER_MAP.get(type));
    }

    @Override
    public List<Page> typeset(Document document) throws TypeSettingException {
        List<List<Paragraph>> consecutiveParagraphLists = convertToParagraphs(document, document.getRoot());

        // Convert headers and footers to paragraph lists for later use during the typesetting
        Map<PageRange, List<List<Paragraph>>> headerParagraphs = convertHeadersOrFootersToParagraphs(document, document.getHeaderNodes());
        Map<PageRange, List<List<Paragraph>>> footerParagraphs = convertHeadersOrFootersToParagraphs(document, document.getFooterNodes());

        // Convert foot notes to paragraph lists for later use during the typesetting
        Map<String, List<List<Paragraph>>> footNoteParagraphs = new HashMap<>();
        for (Map.Entry<String, DocumentNode> entry : document.getFootNotes().entrySet()) {
            footNoteParagraphs.put(entry.getKey(), convertToParagraphs(document, entry.getValue()));
        }

        // Creating context used during typesetting.
        TypeSettingContext ctx = new TypeSettingContext(
                config,
                document,
                consecutiveParagraphLists,
                headerParagraphs,
                footerParagraphs,
                footNoteParagraphs,
                this::typesetConsecutiveParagraphs
        );

        // Type set main document content
        return typesetConsecutiveParagraphs(consecutiveParagraphLists, ctx);
    }

    /**
     * Typeset the passed list of consecutive paragraph lists.
     *
     * @param consecutiveParagraphLists a list of consecutive paragraph lists to typeset
     * @param ctx                       the typesetting context
     * @return the typeset pages
     */
    private List<Page> typesetConsecutiveParagraphs(List<List<Paragraph>> consecutiveParagraphLists, TypeSettingContext ctx) throws TypeSettingException {
        for (List<Paragraph> consecutiveParagraphs : consecutiveParagraphLists) {
            for (Paragraph paragraph : consecutiveParagraphs) {
                ParagraphTypesetHandler handler = KnuthPlassTypeSetter.getHandler(paragraph.getType())
                        .orElseThrow(() -> new TypeSettingException(String.format(
                                "There is no paragraph typesetting handler registered for paragraph type '%s'",
                                paragraph.getType().name()
                        )));

                handler.handle(paragraph, ctx);
            }

            ctx.pushPage(); // Push the current page (due to end of consecutive paragraphs reached - explicit page break).
        }

        return ctx.getPages();
    }

    /**
     * Convert the passed header or footer document root nodes to lists of consecutive paragraphs.
     *
     * @param document to convert node of
     * @param nodeMap  the header or footer document root node mapping
     * @return the converted mapping
     * @throws TypeSettingException in case the headers or footers could not be converted properly
     */
    private Map<PageRange, List<List<Paragraph>>> convertHeadersOrFootersToParagraphs(Document document, Map<PageRange, DocumentNode> nodeMap) throws TypeSettingException {
        Map<PageRange, List<List<Paragraph>>> result = new HashMap<>();

        for (Map.Entry<PageRange, DocumentNode> entry : nodeMap.entrySet()) {
            result.put(entry.getKey(), convertToParagraphs(document, entry.getValue()));
        }

        return result;
    }

    /**
     * Convert the passed document and root node to paragraphs.
     *
     * @param document to convert with
     * @param root     node to convert
     * @return a list of consecutive paragraphs
     * @throws TypeSettingException in case the conversion failed
     */
    private List<List<Paragraph>> convertToParagraphs(Document document, DocumentNode root) throws TypeSettingException {
        try {
            return new KnuthPlassConverter(root, config).convert(document);
        } catch (DocumentConversionException e) {
            throw new TypeSettingException("Could not convert the document into the Knuth-Plass algorithm format", e);
        }
    }

}
