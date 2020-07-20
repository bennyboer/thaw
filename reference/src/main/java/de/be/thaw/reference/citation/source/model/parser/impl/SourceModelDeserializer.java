package de.be.thaw.reference.citation.source.model.parser.impl;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import de.be.thaw.info.model.language.Language;
import de.be.thaw.reference.citation.CitationStyle;
import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.contributor.Author;
import de.be.thaw.reference.citation.source.contributor.Contributor;
import de.be.thaw.reference.citation.source.contributor.NamedContributor;
import de.be.thaw.reference.citation.source.contributor.Organisation;
import de.be.thaw.reference.citation.source.contributor.OtherContributor;
import de.be.thaw.reference.citation.source.impl.Article;
import de.be.thaw.reference.citation.source.impl.Website;
import de.be.thaw.reference.citation.source.impl.book.Book;
import de.be.thaw.reference.citation.source.impl.book.EBook;
import de.be.thaw.reference.citation.source.impl.book.OnlineBook;
import de.be.thaw.reference.citation.source.model.SourceModel;
import de.be.thaw.reference.citation.styles.CitationStyles;
import de.be.thaw.reference.citation.styles.apa.APA;
import de.be.thaw.reference.citation.styles.apa.APASettings;
import de.be.thaw.shared.ThawContext;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Properties;
import java.util.stream.Collectors;

/**
 * Deserializer for the source model.
 */
public class SourceModelDeserializer extends StdDeserializer<SourceModel> {

    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

    public SourceModelDeserializer() {
        this(null);
    }

    protected SourceModelDeserializer(Class<?> vc) {
        super(vc);
    }

    @Override
    public SourceModel deserialize(JsonParser p, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        JsonNode root = p.getCodec().readTree(p);

        SourceModel sourceModel = new SourceModel(parseCitationStyle(root));

        for (JsonNode sourceNode : root.get("sources")) {
            sourceModel.addSource(toSource(sourceNode));
        }

        return sourceModel;
    }

    /**
     * Parse the citation style from the passed node.
     *
     * @param node to get citation style for
     * @return the citation style
     * @throws IOException in case the citation style could not be parsed
     */
    private CitationStyle parseCitationStyle(JsonNode node) throws IOException {
        if (!node.has("style")) {
            // Return default citation style
            return CitationStyles.getDefault();
        }

        JsonNode styleNode = node.get("style");
        if (!styleNode.has("name")) {
            throw new IOException("The 'style' node needs to contain the citation style 'name' attribute");
        }
        String name = styleNode.get("name").asText();

        Optional<CitationStyle> citationStyleOptional = CitationStyles.getCitationStyle(name);
        if (citationStyleOptional.isEmpty()) {
            throw new IOException(String.format("Could not find citation style with name '%s'", name));
        }

        CitationStyle style = citationStyleOptional.get();

        switch (style.getName()) {
            case "APA" -> parseAPASettings(style, styleNode.get("settings"));
        }

        return style;
    }

    /**
     * Parse settings for the APA citation style.
     *
     * @param style to parse settings for
     * @param node  to parse from
     * @throws IOException in case the settings could not be parsed
     */
    private void parseAPASettings(CitationStyle style, @Nullable JsonNode node) throws IOException {
        APA apa = (APA) style;

        Properties properties = new Properties();
        if (node != null && node.has("properties")) {
            // Load custom properties
            File propsFile = new File(ThawContext.getInstance().getRootFolder(), node.get("properties").asText());

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(propsFile), StandardCharsets.UTF_8))) {
                properties.load(br);
            }
        } else {
            // Use properties for the current locale setting
            Language language = ThawContext.getInstance().getLanguage();

            String path = String.format("/i18n/apa/%s.properties", language.getCode());
            InputStream stream = SourceModelDeserializer.class.getResourceAsStream(path);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                properties.load(br);
            }
        }

        apa.setSettings(new APASettings(properties));
    }

    /**
     * Convert from JSON to a source.
     *
     * @param node to convert
     * @return the converted source
     */
    private Source toSource(JsonNode node) throws IOException {
        if (!node.has("type")) {
            throw new IOException("Source entry needs a 'type' field");
        }
        String type = node.get("type").asText().toLowerCase();

        if (!node.has("id")) {
            throw new IOException("Source entry needs a 'id' field");
        }
        String identifier = node.get("id").asText();

        return switch (type) {
            case "book" -> toBook(node, identifier);
            case "onlinebook" -> toOnlineBook(node, identifier);
            case "ebook" -> toEBook(node, identifier);
            case "article" -> toArticle(node, identifier);
            case "website" -> toWebsite(node, identifier);
            default -> throw new IOException(String.format("Source type '%s' is unknown", type));
        };
    }

    /**
     * Parse the passed node to a website source.
     *
     * @param node       to parse
     * @param identifier of the source
     * @return the parsed website source
     * @throws IOException in case the node could not be parsed to a website
     */
    private Website toWebsite(JsonNode node, String identifier) throws IOException {
        if (!node.has("title")) {
            throw new IOException("A website source needs a title");
        }
        if (!node.has("url")) {
            throw new IOException("A website source needs an URL");
        }
        if (!node.has("retrievalDate")) {
            throw new IOException("A website source needs a retrieval date ('retrievalDate')");
        }

        String title = node.get("title").asText();
        String url = node.get("url").asText();

        String retrievalDateStr = node.get("retrievalDate").asText();
        Date retrievalDate;
        try {
            retrievalDate = DATE_FORMAT.parse(retrievalDateStr);
        } catch (ParseException e) {
            throw new IOException("Could not parse date from JSON", e);
        }

        Website website = new Website(identifier, title, url, retrievalDate);

        if (node.has("publicationDate")) {
            String publicationDateStr = node.get("publicationDate").asText();
            Date publicationDate;
            try {
                publicationDate = DATE_FORMAT.parse(publicationDateStr);
            } catch (ParseException e) {
                throw new IOException("Could not parse date from JSON", e);
            }

            website.setPublicationDate(publicationDate);
        }
        if (node.has("contributors")) {
            List<Contributor> contributors = parseContributors(node.get("contributors"));
            List<Author> authors = contributors.stream()
                    .filter(c -> c instanceof Author)
                    .map(c -> (Author) c)
                    .collect(Collectors.toList());

            website.setAuthors(authors);
        }

        return website;
    }

    /**
     * Parse the passed node to an article source.
     *
     * @param node       to parse
     * @param identifier of the source
     * @return the parsed article source
     * @throws IOException in case the node could not be parsed to an article
     */
    private Article toArticle(JsonNode node, String identifier) throws IOException {
        if (!node.has("contributors")) {
            throw new IOException("An article source needs one or more contributors");
        }
        if (!node.has("title")) {
            throw new IOException("An article source needs a title");
        }
        if (!node.has("year")) {
            throw new IOException("An article source needs a year");
        }
        if (!node.has("journalName")) {
            throw new IOException("An article source needs a journalName");
        }
        if (!node.has("volume")) {
            throw new IOException("An article source needs a volume");
        }
        if (!node.has("number")) {
            throw new IOException("An article source needs a number");
        }
        if (!node.has("pages")) {
            throw new IOException("An article source needs a 'pages' page range");
        }
        if (!node.has("doi")) {
            throw new IOException("An article source needs a DOI ('doi')");
        }

        List<Contributor> contributors = parseContributors(node.get("contributors"));
        List<Author> authors = contributors.stream()
                .filter(c -> c instanceof Author)
                .map(c -> (Author) c)
                .collect(Collectors.toList());

        int year = node.get("year").asInt();
        String title = node.get("title").asText();
        String journalName = node.get("journalName").asText();
        int volume = node.get("volume").asInt();
        int number = node.get("number").asInt();
        String pages = node.get("pages").asText();
        String doi = node.get("doi").asText();

        return new Article(
                identifier,
                authors,
                year,
                title,
                journalName,
                volume,
                number,
                pages,
                doi
        );
    }

    /**
     * Parse a node to an ebook.
     *
     * @param node       to parse
     * @param identifier of the source
     * @return the parsed ebook source
     * @throws IOException in case the passed node could not be parsed to an ebook source
     */
    private EBook toEBook(JsonNode node, String identifier) throws IOException {
        OnlineBook book = toOnlineBook(node, identifier);

        EBook eBook = new EBook(book.getIdentifier(), book.getContributors(), book.getTitle(), book.getYear());
        eBook.setCity(book.getCity());
        eBook.setCountry(book.getCountry());
        eBook.setEdition(book.getEdition());
        eBook.setPublisher(book.getPublisher());
        eBook.setUrl(book.getUrl());
        eBook.setDoi(book.getDoi());

        if (node.has("formatInformation")) {
            eBook.setFormatInformation(node.get("formatInformation").asText());
        }

        return eBook;
    }

    /**
     * Parse a node to an online book.
     *
     * @param node       to parse
     * @param identifier of the source
     * @return the online book source
     * @throws IOException in case the passed node could not be parsed to an online book source
     */
    private OnlineBook toOnlineBook(JsonNode node, String identifier) throws IOException {
        Book book = toBook(node, identifier);

        OnlineBook onlineBook = new OnlineBook(book.getIdentifier(), book.getContributors(), book.getTitle(), book.getYear());
        onlineBook.setCity(book.getCity());
        onlineBook.setCountry(book.getCountry());
        onlineBook.setEdition(book.getEdition());
        onlineBook.setPublisher(book.getPublisher());

        if (node.has("url")) {
            onlineBook.setUrl(node.get("url").asText());
        }
        if (node.has("doi")) {
            onlineBook.setDoi(node.get("doi").asText());
        }

        return onlineBook;
    }

    /**
     * Parse a book from the passed node.
     *
     * @param node       to parse to book
     * @param identifier of the source
     * @return the book source
     * @throws IOException in case the node could not be converted to a book
     */
    private Book toBook(JsonNode node, String identifier) throws IOException {
        if (!node.has("contributors")) {
            throw new IOException("A book source needs one or more contributors");
        }
        if (!node.has("title")) {
            throw new IOException("A book source needs a title");
        }
        if (!node.has("year")) {
            throw new IOException("A book source needs a year");
        }

        List<Contributor> contributors = parseContributors(node.get("contributors"));
        String title = node.get("title").asText();
        int year = node.get("year").asInt();

        Book book = new Book(identifier, contributors, title, year);

        if (node.has("edition")) {
            book.setEdition(node.get("edition").asText());
        }
        if (node.has("publisher")) {
            book.setPublisher(node.get("publisher").asText());
        }
        if (node.has("city")) {
            book.setCity(node.get("city").asText());
        }
        if (node.has("country")) {
            book.setCountry(node.get("countrly").asText());
        }

        return book;
    }

    /**
     * Get a list of contributors.
     *
     * @param node to get contributors from
     * @return contributors
     */
    private List<Contributor> parseContributors(JsonNode node) throws IOException {
        List<Contributor> contributors = new ArrayList<>();

        for (JsonNode n : node) {
            contributors.add(parseContributor(n));
        }

        return contributors;
    }

    /**
     * Parse a contributor for the passed JSON node.
     *
     * @param node to parse contributor from
     * @return the parsed contributor
     * @throws IOException in case the contributor could not be parsed
     */
    private Contributor parseContributor(JsonNode node) throws IOException {
        if (node.has("contributor")) {
            // Is either an OtherContributor or an Author
            if (node.has("role")) {
                // Is instance of OtherContributor
                String role = node.get("role").asText();

                return new OtherContributor(parseContributor(node.get("contributor")), role);
            } else {
                return new Author(parseContributor(node.get("contributor")));
            }
        } else {
            // Is either named contributor or an organisation
            if (node.has("firstName") && node.has("lastName")) {
                return new NamedContributor(node.get("firstName").asText(), node.get("lastName").asText());
            } else if (node.has("name")) {
                return new Organisation(node.get("name").asText());
            } else {
                throw new IOException("Could not parse contributor");
            }
        }
    }

}
