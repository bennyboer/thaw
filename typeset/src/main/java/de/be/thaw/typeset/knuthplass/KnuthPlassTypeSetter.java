package de.be.thaw.typeset.knuthplass;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.converter.KnuthPlassConverter;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;
import de.be.thaw.typeset.knuthplass.paragraph.handler.ParagraphTypesetHandler;
import de.be.thaw.typeset.knuthplass.paragraph.handler.impl.ImageParagraphHandler;
import de.be.thaw.typeset.knuthplass.paragraph.handler.impl.TextParagraphHandler;
import de.be.thaw.typeset.page.Page;

import java.util.EnumMap;
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
        // Convert the passed document to a format needed by the Knuth-Plass algorithm.
        List<List<Paragraph>> paragraphs;
        try {
            paragraphs = new KnuthPlassConverter(config).convert(document);
        } catch (DocumentConversionException e) {
            throw new TypeSettingException("Could not convert the document into the Knuth-Plass algorithm format", e);
        }

        // Creating context used during typesetting.
        TypeSettingContext ctx = new TypeSettingContext(config, paragraphs);

        for (List<Paragraph> consecutiveParagraphs : paragraphs) {
            ctx.getFloatConfig().reset();

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

}
