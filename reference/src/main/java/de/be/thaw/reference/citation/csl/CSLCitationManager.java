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
import de.be.thaw.shared.ThawContext;
import de.be.thaw.util.cache.CacheUtil;
import de.be.thaw.util.cache.exception.CouldNotGetProjectCacheDirectoryException;
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
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Properties;
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
     * Cache location under user.home/.thaw
     */
    private static final String CACHE_LOCATION = "citation";

    /**
     * Version of the cache.
     * When the versions mismatch, the cache will have to be invalidated.
     */
    private static final int CACHE_VERSION = 1;

    /**
     * Name of the cache file for citations.
     */
    private static final String CITATION_CACHE_FILE_NAME = "citations";

    /**
     * Name for the cache file for the bibliography.
     */
    private static final String BIBLIOGRAPHY_CACHE_FILE_NAME = "bibliography";

    /**
     * Name of the cache info file.
     */
    private static final String CACHE_INFO_FILE_NAME = "info";

    /**
     * Key in the bibliography cache file that lists the cached source IDs as value.
     */
    private static final String BIBLIOGRAPHY_SOURCE_IDS_KEY = "source-ids";

    /**
     * The citation style to use, loaded lazily using the getCsl() method.
     */
    @Nullable
    private CSL csl;

    /**
     * Citation style name to use.
     */
    private final String citationStyleName;

    /**
     * Language code to use for the citation style.
     */
    private final String citationStyleLanguageCode;

    /**
     * The bibliography file to load from.
     */
    private final File bibliographyFile;

    /**
     * Provider for items in the bibliography.
     */
    private final ItemDataProvider provider;

    /**
     * All available source IDs in the bibliography.
     */
    private final Set<String> sourceIDs;

    /**
     * Cached citations (Hash of the source item mapped to resulting citation string).
     */
    private final HashMap<String, String> cachedCitations = new HashMap<>();

    /**
     * Set of cited source IDs.
     */
    private final Set<String> citedSourceIDs = new HashSet<>();

    /**
     * Source IDs that are listed in the cached bibliography.
     */
    private final Set<String> cachedBibliographySourceIDs = new HashSet<>();

    /**
     * The cache directory to use for caching citations and bibliographies.
     */
    private File cacheDir;

    /**
     * Whether the reference list is cached.
     */
    private boolean hasReferenceListCached = false;

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
        this.citationStyleName = citationStyleName;
        this.citationStyleLanguageCode = language.getLocale().toString().replace("_", "-");
        this.bibliographyFile = bibliography;

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
            throw new UnsupportedCitationStyleException("Could not supported citation styles");
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

        // Load cached citations and bibliographies
        try {
            loadCache();
        } catch (IOException e) {
            throw new CouldNotLoadBibliographyException("Could not load cached citations and bibliographies", e);
        }
    }

    /**
     * Get the CSL to generate citations and bibliographies.
     *
     * @return csl
     */
    private CSL getCsl() throws CouldNotLoadBibliographyException {
        if (csl == null) {
            try {
                long timer = System.nanoTime();

                csl = new CSL(provider, citationStyleName, citationStyleLanguageCode);
                csl.setOutputFormat("text");

                if (Debug.isDebug()) {
                    LOGGER.log(Level.INFO, String.format(
                            "Loading citeproc-java CSL took %d ms",
                            (System.nanoTime() - timer) / 1_000_000
                    ));
                }
            } catch (IOException e) {
                throw new CouldNotLoadBibliographyException(String.format(
                        "Citation style could not be loaded: '%s'",
                        e.getMessage()
                ), e);
            }
        }

        return csl;
    }

    /**
     * Check whether we are allowed to load citations and bibliography from cache.
     * For example the bibliography file may have changed or the style is another than last time.
     *
     * @param cacheDir the cache folder to lookup into
     * @return whether we can load from cache
     */
    private boolean canLoadFromCache(File cacheDir) throws IOException {
        File cacheInfoFile = new File(cacheDir, CACHE_INFO_FILE_NAME);

        // Calculate hash of the current bibliography file
        String currentHash;
        try {
            currentHash = CacheUtil.generateHexHash(new FileInputStream(bibliographyFile));
        } catch (NoSuchAlgorithmException e) {
            throw new IOException(e);
        }

        boolean canLoadFromCache = true;
        if (!cacheInfoFile.exists()) {
            cacheInfoFile.createNewFile();

            if (Debug.isDebug()) {
                LOGGER.log(Level.INFO, String.format(
                        "Citation and bibliography cache info file does not yet exist for the project. Creating it at '%s'.",
                        cacheInfoFile.getAbsolutePath()
                ));
            }
        } else {
            Properties properties = new Properties();
            properties.load(new FileInputStream(cacheInfoFile));

            String cacheVersionStr = properties.getProperty("cache.version");
            if (cacheVersionStr == null) {
                canLoadFromCache = false;
            } else {
                try {
                    int oldCacheVersion = Integer.parseInt(properties.getProperty("cache.version"));
                    if (oldCacheVersion != CACHE_VERSION) {
                        canLoadFromCache = false;
                    }
                } catch (NumberFormatException e) {
                    canLoadFromCache = false;
                }
            }

            String oldBibliographyStyleName = properties.getProperty("bibliography.style");
            if (!citationStyleName.equalsIgnoreCase(oldBibliographyStyleName)) {
                canLoadFromCache = false;
            }

            String oldBibliographyLanguageCode = properties.getProperty("bibliography.lang");
            if (!citationStyleLanguageCode.equalsIgnoreCase(oldBibliographyLanguageCode)) {
                canLoadFromCache = false;
            }

            String oldBibliographyFileHash = properties.getProperty("bibliography.hash");
            if (!currentHash.equals(oldBibliographyFileHash)) {
                canLoadFromCache = false;
            }
        }

        // Renew cache info file
        Properties properties = new Properties();

        properties.setProperty("cache.version", String.valueOf(CACHE_VERSION));
        properties.setProperty("bibliography.style", citationStyleName);
        properties.setProperty("bibliography.lang", citationStyleLanguageCode);
        properties.setProperty("bibliography.hash", currentHash);

        properties.store(new FileOutputStream(cacheInfoFile), "Info about the projects citation and bibliography cache");

        return canLoadFromCache;
    }

    /**
     * Load all cached citations and bibliographies.
     */
    private void loadCache() throws IOException {
        // Load a project-specific cache directory.
        File projectCacheDir;
        try {
            projectCacheDir = CacheUtil.getProjectSpecificCacheDir(ThawContext.getInstance().getRootFolder());
        } catch (CouldNotGetProjectCacheDirectoryException e) {
            throw new IOException(e);
        }

        // Create or get existing cache directory for citations.
        cacheDir = new File(projectCacheDir, CACHE_LOCATION);
        if (!cacheDir.exists()) {
            cacheDir.mkdir();
        }

        // Check if loading from cached is allowed (bibliography file did not change compared to last pass, style did not change).
        if (!canLoadFromCache(cacheDir)) {
            if (Debug.isDebug()) {
                LOGGER.log(Level.INFO, "Citation and bibliography cache is invalid and will not be used");
            }
            return;
        }

        // Loading citation cache.
        File citationCacheFile = new File(cacheDir, CITATION_CACHE_FILE_NAME);
        cachedCitations.clear();
        if (citationCacheFile.exists()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(citationCacheFile));

            for (Map.Entry<Object, Object> entry : properties.entrySet()) {
                cachedCitations.put((String) entry.getKey(), (String) entry.getValue());
            }
        }

        // Loading bibliography cache.
        cachedBibliographySourceIDs.clear();
        File bibliographyCacheFile = new File(cacheDir, BIBLIOGRAPHY_CACHE_FILE_NAME);
        if (bibliographyCacheFile.exists()) {
            Properties properties = new Properties();
            properties.load(new FileInputStream(bibliographyCacheFile));

            String cachedSourceIds = properties.getProperty(BIBLIOGRAPHY_SOURCE_IDS_KEY);
            if (cachedSourceIds != null) {
                for (String id : cachedSourceIds.split(",")) {
                    cachedBibliographySourceIDs.add(id.trim());
                }
            }

            hasReferenceListCached = true;
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
    public String register(List<Citation> citations) throws MissingSourceException, CouldNotLoadBibliographyException {
        String citationHash = generateCitationHash(citations);

        // Mark all citations source IDs as cited.
        for (Citation c : citations) {
            citedSourceIDs.add(c.getSourceID());
        }

        // Check if citation has already been cached
        Optional<String> cachedCitation = getCachedCitation(citationHash);
        if (cachedCitation.isPresent()) {
            return cachedCitation.orElseThrow();
        }

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

        // Generate the citation
        List<de.undercouch.citeproc.output.Citation> output;
        try {
            output = getCsl().makeCitation(new CSLCitation(
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

        // Generate citation string
        String result = output.stream()
                .map(de.undercouch.citeproc.output.Citation::getText)
                .collect(Collectors.joining());

        // Cache resulting citation string
        writeCitationToCache(result, citationHash);

        return result;
    }

    /**
     * Get a cached citation by the passed hash.
     *
     * @param hash to get citation by
     * @return the citation or an empty optional if not found
     */
    private Optional<String> getCachedCitation(String hash) {
        return Optional.ofNullable(cachedCitations.get(hash));
    }

    /**
     * Write the passed citation string to the cache.
     *
     * @param citationString to cache
     * @param hash           of the source the citation string was generated from
     */
    private void writeCitationToCache(String citationString, String hash) {
        cachedCitations.put(hash, citationString);
    }

    /**
     * Update the citation cache file.
     *
     * @throws IOException in case the cache could not be updated
     */
    private void updateCitationCacheFile() throws IOException {
        File citationCacheFile = new File(cacheDir, CITATION_CACHE_FILE_NAME);
        citationCacheFile.createNewFile();

        Properties properties = new Properties();
        for (Map.Entry<String, String> entry : cachedCitations.entrySet()) {
            properties.setProperty(entry.getKey(), entry.getValue());
        }

        properties.store(new FileOutputStream(citationCacheFile), "Cached in-text citation strings");
    }

    /**
     * Update the bibliography cache files to represent the current state.
     *
     * @param referenceList to save
     */
    private void updateBibliographyCacheFile(ReferenceList referenceList) throws IOException {
        File bibliographyCacheFile = new File(cacheDir, BIBLIOGRAPHY_CACHE_FILE_NAME);
        bibliographyCacheFile.createNewFile();

        Properties properties = new Properties();

        // Save cited source ids in a list
        properties.setProperty(BIBLIOGRAPHY_SOURCE_IDS_KEY, String.join(",", citedSourceIDs));

        // Save all entries
        for (ReferenceListEntry entry : referenceList.getEntries()) {
            properties.setProperty(String.format("entry.%s", entry.getSourceID()), entry.getText());
        }

        // Save settings
        properties.setProperty("settings.hanging-indent", String.valueOf(referenceList.getHangingIndent()));
        properties.setProperty("settings.entry-spacing", String.valueOf(referenceList.getEntrySpacing()));

        properties.store(new FileOutputStream(bibliographyCacheFile), "Cached bibliography info");
    }

    /**
     * Generate a hash for the passed citations.
     *
     * @param citations to generate hash for
     * @return the generated hash
     */
    private String generateCitationHash(List<Citation> citations) {
        if (citations.size() == 1) {
            return citations.get(0).generateHash();
        } else {
            StringBuilder sb = new StringBuilder();

            for (Citation c : citations) {
                sb.append(c.generateHash());
            }

            return sb.toString();
        }
    }

    @Override
    public boolean hasSource(String sourceID) {
        return sourceIDs.contains(sourceID);
    }

    /**
     * Load the reference list/bibliography from cache.
     *
     * @return the cached reference list/bibliography
     * @throws IOException in case the reference list could not be loaded from cache
     */
    private ReferenceList loadReferenceListFromCache() throws IOException {
        File bibliographyCacheFile = new File(cacheDir, BIBLIOGRAPHY_CACHE_FILE_NAME);
        if (!bibliographyCacheFile.exists()) {
            throw new IOException("Could not load reference list/bibliography from cache as the file does not exist");
        }

        Properties properties = new Properties();
        properties.load(new FileInputStream(bibliographyCacheFile));

        // Load entries
        List<ReferenceListEntry> entries = new ArrayList<>();
        for (String sourceID : cachedBibliographySourceIDs) {
            String text = properties.getProperty(String.format("entry.%s", sourceID));

            entries.add(new ReferenceListEntry(sourceID, text));
        }

        ReferenceList referenceList = new ReferenceList(entries);

        // Load bibliography settings
        referenceList.setHangingIndent(Boolean.parseBoolean(properties.getProperty("settings.hanging-indent")));
        referenceList.setEntrySpacing(Double.parseDouble(properties.getProperty("settings.entry-spacing")));

        return referenceList;
    }

    @Override
    public ReferenceList buildReferenceList() throws CouldNotLoadBibliographyException, IOException {
        updateCitationCacheFile();

        boolean loadFromCache = hasReferenceListCached && cachedBibliographySourceIDs.equals(citedSourceIDs);
        if (loadFromCache) {
            return loadReferenceListFromCache();
        }

        if (Debug.isDebug()) {
            LOGGER.log(Level.INFO, "The reference list/bibliography is regenerated...");
        }

        // Make sure that citations for source IDs that have been restored are still registered in the citation style
        if (!citedSourceIDs.isEmpty()) {
            getCsl().registerCitationItems(citedSourceIDs.toArray(String[]::new));
        }

        // Create bibliography
        getCsl().setOutputFormat("html");
        Bibliography bib = getCsl().makeBibliography();

        // TODO for bib.getSecondFieldAlign() (for example when using the ieee style) we need tables!

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

        // Update reference list/bibliography cache
        updateBibliographyCacheFile(referenceList);

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

        return sb.toString().trim();
    }

    /**
     * Escape characters of the Thaw document text format in the passed string.
     *
     * @param str to escape characters in
     * @return the escaped string
     */
    private String escapeStringForTDT(final String str) {
        char[] toEscape = new char[]{'_', '*', '#', '`'};

        StringBuilder sb = new StringBuilder(str);
        for (char toEscapeChar : toEscape) {
            String toEscapeCharStr = String.valueOf(toEscapeChar);

            int index = sb.indexOf(toEscapeCharStr);
            while (index != -1) {
                // Check if already escaped
                char before = index > 0 ? sb.charAt(index - 1) : ' ';
                if (before != '\\') {
                    // Escape char
                    sb.insert(index, '\\');
                    index++;
                }

                index = sb.indexOf(toEscapeCharStr, index + 1);
            }
        }

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
                textConsumer.accept(escapeStringForTDT(((TextNode) child).text()));
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
