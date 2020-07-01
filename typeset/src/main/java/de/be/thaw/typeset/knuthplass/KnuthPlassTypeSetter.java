package de.be.thaw.typeset.knuthplass;

import de.be.thaw.core.document.Document;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.knuthplass.config.LineBreakingConfig;
import de.be.thaw.typeset.knuthplass.converter.KnuthPlassConverter;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.page.Page;

import java.util.List;

/**
 * Implementation of the Knuth-Plass line breaking algorithm.
 */
public class KnuthPlassTypeSetter implements TypeSetter {

    /**
     * Configuration of the
     */
    private final LineBreakingConfig config;

    /**
     * Paragraphs to type set.
     */
    private List<Paragraph> paragraphs;

    public KnuthPlassTypeSetter(LineBreakingConfig config) {
        this.config = config;
    }

    @Override
    public List<Page> typeset(Document document) {
        paragraphs = new KnuthPlassConverter(config).convert(document);

        // TODO

        return null;
    }

}
