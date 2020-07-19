package de.be.thaw.reference.citation.styles.apa;

import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.CitationStyle;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.styles.apa.handler.AbstractAPAHandler;
import de.be.thaw.reference.citation.styles.apa.handler.ArticleHandler;
import de.be.thaw.reference.citation.styles.apa.handler.BookHandler;
import de.be.thaw.reference.citation.styles.apa.handler.EBookHandler;
import de.be.thaw.reference.citation.styles.apa.handler.OnlineBookHandler;
import de.be.thaw.reference.citation.styles.apa.handler.WebsiteHandler;
import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;
import de.be.thaw.reference.citation.styles.exception.UnsupportedSourceTypeException;
import de.be.thaw.reference.citation.styles.referencelist.ReferenceListEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * The APA citation style.
 */
public class APA implements CitationStyle {

    /**
     * Mapping of source types to the handlers that are able
     * to deal with them.
     */
    private static final Map<SourceType, AbstractAPAHandler> SOURCE_HANDLERS = new EnumMap<>(SourceType.class);

    static {
        initSourceHandler(new BookHandler());
        initSourceHandler(new EBookHandler());
        initSourceHandler(new OnlineBookHandler());
        initSourceHandler(new ArticleHandler());
        initSourceHandler(new WebsiteHandler());
    }

    /**
     * Initialize the passed source handler.
     *
     * @param handler to initialize
     */
    private static void initSourceHandler(AbstractAPAHandler handler) {
        for (SourceType type : handler.supports()) {
            SOURCE_HANDLERS.put(type, handler);
        }
    }

    /**
     * Get a source handler for the passed type.
     *
     * @param type to get source handler for
     * @return source handler
     */
    private static Optional<AbstractAPAHandler> getSourceHandler(SourceType type) {
        return Optional.ofNullable(SOURCE_HANDLERS.get(type));
    }

    /**
     * Settings for the citation style.
     */
    private APASettings settings;

    /**
     * Mapping of source identifiers to their reference list entry.
     */
    private final Map<String, String> referenceListEntries = new HashMap<>();

    public APA() {
        this(new APASettings());
    }

    public APA(APASettings settings) {
        this.settings = settings;
    }

    /**
     * Get the settings.
     *
     * @return settings
     */
    public APASettings getSettings() {
        return settings;
    }

    /**
     * Set the APA specific settings.
     *
     * @param settings to set
     */
    public void setSettings(APASettings settings) {
        this.settings = settings;
    }

    @Override
    public String getName() {
        return "APA";
    }

    @Override
    public List<ReferenceListEntry> getReferenceListEntries() {
        return referenceListEntries.entrySet()
                .stream()
                .map(e -> new ReferenceListEntry(e.getKey(), e.getValue()))
                .sorted(Comparator.comparing(ReferenceListEntry::getEntry))
                .collect(Collectors.toList());
    }

    @Override
    public String addCitation(List<Citation> citations) throws UnsupportedSourceTypeException, ReferenceBuildException {
        List<String> parts = new ArrayList<>(citations.size());

        for (Citation citation : citations) {
            AbstractAPAHandler handler = getSourceHandler(citation.getSource().getType()).orElseThrow(() -> new UnsupportedSourceTypeException(String.format(
                    "Could not build in-text-citation because the source type '%s' is not supported by the citation style",
                    citation.getSource().getType().name()
            )));
            handler.setSettings(settings);

            // Add reference list entry (if not already there)
            if (!referenceListEntries.containsKey(citation.getSource().getIdentifier())) {
                referenceListEntries.put(citation.getSource().getIdentifier(), handler.buildReferenceListEntry(citation.getSource()));
            }

            parts.add(handler.buildInTextCitation(citation));
        }

        // Sort sources alphabetically
        Collections.sort(parts);

        // Separate the individual sources by semicolons
        String inTextCitation = String.join("; ", parts);

        boolean isDirect = citations.get(0).isDirect();
        if (isDirect) {
            return inTextCitation;
        } else {
            return String.format("(%s)", inTextCitation);
        }
    }

}
