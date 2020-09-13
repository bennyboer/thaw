package de.be.thaw.reference.citation.csl;

import de.be.thaw.info.model.language.Language;
import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.CitationManager;
import de.be.thaw.reference.citation.exception.CouldNotLoadBibliographyException;
import de.be.thaw.reference.citation.exception.MissingSourceException;
import de.be.thaw.reference.citation.exception.UnsupportedBibliographyFormatException;
import de.be.thaw.reference.citation.exception.UnsupportedCitationStyleException;
import de.be.thaw.reference.citation.referencelist.ReferenceList;
import de.be.thaw.reference.citation.referencelist.ReferenceListEntry;
import de.be.thaw.util.debug.Debug;
import de.undercouch.citeproc.CSL;
import de.undercouch.citeproc.ItemDataProvider;
import de.undercouch.citeproc.bibtex.BibTeXConverter;
import de.undercouch.citeproc.bibtex.BibTeXItemDataProvider;
import de.undercouch.citeproc.csl.CSLCitation;
import de.undercouch.citeproc.csl.CSLCitationItem;
import de.undercouch.citeproc.csl.CSLCitationItemBuilder;
import de.undercouch.citeproc.csl.CSLLabel;
import de.undercouch.citeproc.csl.CSLProperties;
import de.undercouch.citeproc.endnote.EndNoteConverter;
import de.undercouch.citeproc.endnote.EndNoteItemDataProvider;
import de.undercouch.citeproc.endnote.EndNoteLibrary;
import de.undercouch.citeproc.output.Bibliography;
import de.undercouch.citeproc.ris.RISConverter;
import de.undercouch.citeproc.ris.RISItemDataProvider;
import de.undercouch.citeproc.ris.RISLibrary;
import org.jbibtex.BibTeXDatabase;
import org.jbibtex.ParseException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Citation manager using CLS by leveraging the citeproc-java library.
 */
public class CSLCitationManager implements CitationManager {

    /**
     * Logger of the class.
     */
    private static final Logger LOGGER = Logger.getLogger(CSLCitationManager.class.getSimpleName());

    /**
     * The citation style to use.
     */
    private final CSL csl;

    /**
     * Provider for items in the bibliography.
     */
    private final ItemDataProvider provider;

    /**
     * All available source IDs in the bibliography.
     */
    private final Set<String> sourceIDs;

    /**
     * Create new CSL citation manager.
     *
     * @param bibliography      file to use
     * @param citationStyleName the citation style name (e. g. "ieee", "apa", ...)
     * @param language          used for localization
     * @throws UnsupportedBibliographyFormatException in case the bibliography file format is unsupported
     * @throws CouldNotLoadBibliographyException      in case the bibliography could not be loaded from file
     * @throws UnsupportedCitationStyleException      in case the given citation style is not supported
     */
    public CSLCitationManager(File bibliography, String citationStyleName, Language language)
            throws UnsupportedBibliographyFormatException, CouldNotLoadBibliographyException, UnsupportedCitationStyleException {
        citationStyleName = citationStyleName.toLowerCase();

        long timer = System.nanoTime();

        // Check if the citation style specified is supported
        try {
            if (!CSL.getSupportedStyles().contains(citationStyleName)) {
                throw new UnsupportedCitationStyleException(String.format(
                        "Citation style '%s' is not supported. Pick one of the following:%n%s",
                        citationStyleName,
                        CSL.getSupportedStyles().stream()
                                .sorted()
                                .collect(Collectors.joining("\n"))
                ));
            }
        } catch (IOException e) {
            throw new UnsupportedCitationStyleException("Could not load supported citation styles");
        }

        // Load bibliography from file
        if (bibliography.getName().endsWith(".bib")) {
            provider = loadBibTeXBibliography(bibliography);
        } else if (bibliography.getName().endsWith(".enl")) {
            provider = loadEndNoteBibliography(bibliography);
        } else if (bibliography.getName().endsWith(".ris")) {
            provider = loadRISBibliography(bibliography);
        } else {
            throw new UnsupportedBibliographyFormatException(String.format(
                    "The bibliography file at '%s' does not seem to be either in BibTeX (*.bib), EndNote (*.enl) or RIS (*.ris) format.",
                    bibliography.getAbsolutePath()
            ));
        }

        // Load all available source IDs
        sourceIDs = new HashSet<>();
        sourceIDs.addAll(Arrays.asList(provider.getIds()));

        // Load citation style
        try {
            csl = new CSL(provider, citationStyleName, language.getLocale().toString().replace("_", "-"));
            csl.setOutputFormat("text");
        } catch (IOException e) {
            throw new CouldNotLoadBibliographyException(String.format(
                    "Citation style could not be loaded: '%s'",
                    e.getMessage()
            ), e);
        }

        if (Debug.isDebug()) {
            LOGGER.log(Level.INFO, String.format(
                    "Loading the CSL citation manager took %d ms",
                    (System.nanoTime() - timer) / 1_000_000
            ));
        }
    }

    /**
     * Load a BibTeX bibliography file.
     *
     * @param bibliography to load
     * @return the data provider for the citation style
     * @throws CouldNotLoadBibliographyException in case the file could not be loaded
     */
    private ItemDataProvider loadBibTeXBibliography(File bibliography) throws CouldNotLoadBibliographyException {
        BibTeXDatabase database;
        try {
            database = new BibTeXConverter().loadDatabase(new FileInputStream(bibliography));
        } catch (ParseException | FileNotFoundException e) {
            throw new CouldNotLoadBibliographyException(String.format(
                    "BibTeX bibliography file at '%s' could not be loaded: '%s'",
                    bibliography.getAbsolutePath(),
                    e.getMessage()
            ), e);
        }

        BibTeXItemDataProvider newProvider = new BibTeXItemDataProvider();
        newProvider.addDatabase(database);

        return newProvider;
    }

    /**
     * Load a EndNote bibliography file.
     *
     * @param bibliography to load
     * @return the data provider for the citation style
     * @throws CouldNotLoadBibliographyException in case the file could not be loaded
     */
    private ItemDataProvider loadEndNoteBibliography(File bibliography) throws CouldNotLoadBibliographyException {
        EndNoteLibrary library;
        try {
            library = new EndNoteConverter().loadLibrary(new FileInputStream(bibliography));
        } catch (IOException | ParseException e) {
            throw new CouldNotLoadBibliographyException(String.format(
                    "EndNote library file at '%s' could not be loaded: '%s'",
                    bibliography.getAbsolutePath(),
                    e.getMessage()
            ), e);
        }

        EndNoteItemDataProvider newProvider = new EndNoteItemDataProvider();
        newProvider.addLibrary(library);

        return newProvider;
    }

    /**
     * Load a RIS bibliography file.
     *
     * @param bibliography to load
     * @return the data provider for the citation style
     * @throws CouldNotLoadBibliographyException in case the file could not be loaded
     */
    private ItemDataProvider loadRISBibliography(File bibliography) throws CouldNotLoadBibliographyException {
        RISLibrary library;
        try {
            library = new RISConverter().loadLibrary(new FileInputStream(bibliography));
        } catch (IOException | ParseException e) {
            throw new CouldNotLoadBibliographyException(String.format(
                    "RIS library file at '%s' could not be loaded: '%s'",
                    bibliography.getAbsolutePath(),
                    e.getMessage()
            ), e);
        }

        RISItemDataProvider newProvider = new RISItemDataProvider();
        newProvider.addLibrary(library);

        return newProvider;
    }

    @Override
    public String register(List<Citation> citations) throws MissingSourceException {
        // Map citations to the CSLCitationItems
        CSLCitationItem[] items = citations.stream().map(c -> new CSLCitationItemBuilder(c.getSourceID())
                .label(c.getLabel().map(String::toLowerCase).map(CSLLabel::fromString).orElse(null))
                .locator(c.getLocator().orElse(null))
                .prefix(c.getPrefix().orElse(null))
                .suffix(c.getSuffix().orElse(null))
                .nearNote(c.isNearNote().orElse(null))
                .authorOnly(c.isAuthorOnly())
                .suppressAuthor(c.isSuppressAuthor())
                .build()
        ).toArray(CSLCitationItem[]::new);

        List<de.undercouch.citeproc.output.Citation> output;
        try {
            output = csl.makeCitation(new CSLCitation(
                    items,
                    UUID.randomUUID().toString(),
                    new CSLProperties()
            ));
        } catch (IllegalArgumentException e) {
            throw new MissingSourceException(String.format(
                    "Could not find all needed source IDs '%s' in the provided bibliography",
                    citations.stream().map(Citation::getSourceID).collect(Collectors.joining(", "))
            ), e);
        }

        return output.stream()
                .map(de.undercouch.citeproc.output.Citation::getText)
                .collect(Collectors.joining());
    }

    @Override
    public boolean hasSource(String sourceID) {
        return sourceIDs.contains(sourceID);
    }

    @Override
    public ReferenceList buildReferenceList() {
        csl.setOutputFormat("html");
        Bibliography bib = csl.makeBibliography();

        // Convert entries from HTML to the Thaw document text format.
        List<ReferenceListEntry> entries = new ArrayList<>();
        String[] bibEntries = bib.getEntries();
        String[] bibEntryIds = bib.getEntryIds();
        for (int i = 0; i < bibEntries.length; i++) {
            String sourceID = bibEntryIds[i];
            String bibEntryHTML = bibEntries[i];

            entries.add(new ReferenceListEntry(sourceID, convertHTMLtoTDT(bibEntryHTML)));
        }

        // Create reference list and apply additional settings.
        ReferenceList referenceList = new ReferenceList(entries);
        referenceList.setEntrySpacing(bib.getEntrySpacing());
        referenceList.setHangingIndent(bib.getHangingIndent());

        return referenceList;
    }

    /**
     * Convert the passed HTML string to the Thaw document text format.
     *
     * @param html to convert
     * @return tdt format text
     */
    private String convertHTMLtoTDT(String html) {
        Document document = Jsoup.parseBodyFragment(html);
        StringBuilder sb = new StringBuilder();

        convertHTMLtoTDTForElement(document.body().getElementsByClass("csl-entry").first(), sb::append);

        return sb.toString();
    }

    /**
     * Convert the passed HTML element to the Thaw document text format.
     *
     * @param element      to process
     * @param textConsumer to publish tdt text
     */
    private void convertHTMLtoTDTForElement(Element element, Consumer<String> textConsumer) {
        for (Node child : element.childNodes()) {
            if (child instanceof TextNode) {
                textConsumer.accept(((TextNode) child).text());
            } else if (child instanceof Element) {
                Element childElement = (Element) child;

                String tagName = childElement.tagName().toLowerCase();
                boolean isBold = false;
                boolean isItalic = false;
                switch (tagName) {
                    case "strong", "b" -> isBold = true;
                    case "i" -> isItalic = true;
                }

                if (isBold) {
                    textConsumer.accept("**");
                }
                if (isItalic) {
                    textConsumer.accept("*");
                }

                convertHTMLtoTDTForElement(childElement, textConsumer);

                if (isBold) {
                    textConsumer.accept("**");
                }
                if (isItalic) {
                    textConsumer.accept("*");
                }
            }
        }
    }

}
