package de.be.thaw.reference.citation.styles.apa;

import de.be.thaw.reference.citation.Citation;
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
import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;
import de.be.thaw.reference.citation.styles.exception.UnsupportedSourceTypeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * Test of the APA citation style implementation.
 */
public class APATest {

    private static final List<Contributor> SINGLE_AUTHOR = Collections.singletonList(toAuthor("Max Mustermann"));

    private static final List<Contributor> SINGLE_ORGANISATION_AUTHOR = Collections.singletonList(new Author(new Organisation("Thaw Inc.")));

    private static final List<Contributor> MIXED_AUTHOR_TYPES = Arrays.asList(new Author(new Organisation("Thaw Inc.")), toAuthor("Max Mustermann"));

    private static final List<Contributor> THREE_AUTHORS = Arrays.asList(
            toAuthor("Max Mustermann"),
            toAuthor("John Doe"),
            toAuthor("Benjamin Eder")
    );

    private static final List<Contributor> FIVE_AUTHORS = Arrays.asList(
            toAuthor("Max Mustermann"),
            toAuthor("John Doe"),
            toAuthor("Benjamin Eder"),
            toAuthor("Another Name"),
            toAuthor("Walt Disney")
    );

    private static final List<Contributor> COMPLEX_CONTRIBUTORS = Arrays.asList(
            toAuthor("Max Mustermann"),
            toAuthor("John Doe"),
            toAuthor("Benjamin Eder"),
            new Author(new Organisation("Walt Disney Enterprises")),
            toAuthor("Another Name"),
            new OtherContributor(new NamedContributor("Another", "Name"), "Translator"),
            new OtherContributor(new NamedContributor("John", "Editing"), "Editor"),
            new OtherContributor(new NamedContributor("Walt", "Disney"), "Editor")
    );

    private static Author toAuthor(String name) {
        String[] parts = name.split(" ");

        return new Author(new NamedContributor(parts[0], parts[1]));
    }

    private APA apa;

    @BeforeEach
    public void setup() {
        APASettings settings = new APASettings();
        settings.setDateFormat(new SimpleDateFormat("yyyy-MM-dd"));

        apa = new APA(settings);
    }

    @Test
    public void singleAuthorBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("identifier", SINGLE_AUTHOR, "My fantastic book title", 2020);

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann, 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M. (2020). *My fantastic book title*.", referenceListEntry);
    }

    @Test
    public void multipleAuthorsBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("identifier", THREE_AUTHORS, "My fantastic book title", 2020);

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann, Doe & Eder, 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M., Doe, J. & Eder, B. (2020). *My fantastic book title*.", referenceListEntry);

        String inTextCitationDirect = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Mustermann, Doe and Eder (2020)", inTextCitationDirect);
    }

    @Test
    public void organisationAuthorBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("id", SINGLE_ORGANISATION_AUTHOR, "My fantastic book title", 2020);

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Thaw Inc., 2020)", inTextCitation);
        Assertions.assertEquals("Thaw Inc. (2020). *My fantastic book title*.", referenceListEntry);
    }

    @Test
    public void mixedAuthorTypeBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("id", MIXED_AUTHOR_TYPES, "My fantastic book title", 2020);

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Thaw Inc. & Mustermann, 2020)", inTextCitation);
        Assertions.assertEquals("Thaw Inc. & Mustermann, M. (2020). *My fantastic book title*.", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Thaw Inc. and Mustermann (2020)", directInTextCitation);
    }

    @Test
    public void aLotOfAuthorsBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("id", FIVE_AUTHORS, "My fantastic book title", 2020);

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann et al., 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Name, A. & Disney, W. (2020). *My fantastic book title*.", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Mustermann et al. (2020)", directInTextCitation);
    }

    @Test
    public void complexBookTest1() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("id", COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setEdition("2nd Edition");
        book.setCity("Munich");
        book.setCountry("Germany");
        book.setPublisher("Thaw");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann et al., 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor) (2nd Edition). Munich, Germany: Thaw", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Mustermann et al. (2020)", directInTextCitation);
    }

    @Test
    public void complexBookTest2() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("id", COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setPublisher("Thaw");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann et al., 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor). Thaw", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Mustermann et al. (2020)", directInTextCitation);
    }

    @Test
    public void complexBookTest3() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("id", COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setCountry("Germany");
        book.setPublisher("Thaw");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann et al., 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor). Germany: Thaw", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Mustermann et al. (2020)", directInTextCitation);
    }

    @Test
    public void simpleInTextBookCitation() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book("id", SINGLE_AUTHOR, "My fantastic book title", 2020);

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        Assertions.assertEquals("(Mustermann, 2020)", inTextCitation);

        String inTextCitation2 = apa.addCitation(Collections.singletonList(new Citation(book, false, "p. 43")));
        Assertions.assertEquals("(Mustermann, 2020, p. 43)", inTextCitation2);

        String inTextCitation3 = apa.addCitation(Collections.singletonList(new Citation(book, true, "p. 43")));
        Assertions.assertEquals("Mustermann (2020, p. 43)", inTextCitation3);
    }

    @Test
    public void complexInTextBookCitation() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book1 = new Book("id", COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book1.setEdition("2nd Edition");
        book1.setCity("Munich");
        book1.setCountry("Germany");
        book1.setPublisher("Thaw");

        Book book2 = new Book("id2", SINGLE_ORGANISATION_AUTHOR, "Another book", 1964);

        Book book3 = new Book("id3", Collections.singletonList(new Author(new NamedContributor("Lewis", "Carroll"))), "Alice in wonderland", 2008);

        String inTextCitation = apa.addCitation(Arrays.asList(new Citation(book1, false, "p. 1"), new Citation(book2, false, "p. 43"), new Citation(book3)));

        Assertions.assertEquals("(Carroll, 2008; Mustermann et al., 2020, p. 1; Thaw Inc., 1964, p. 43)", inTextCitation);
        Assertions.assertEquals(3, apa.getReferenceListEntries().size());

        String directInTextCitation = apa.addCitation(Arrays.asList(new Citation(book1, true, "p. 1"), new Citation(book2, true, "p. 43"), new Citation(book3, true)));

        Assertions.assertEquals("Carroll (2008); Mustermann et al. (2020, p. 1); Thaw Inc. (1964, p. 43)", directInTextCitation);
    }

    @Test
    public void simpleEbookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        EBook book = new EBook("id", SINGLE_AUTHOR, "My fantastic ebook title", 2020);
        book.setDoi("10.1109/5.771073");
        book.setUrl("https://ieeexplore.ieee.org/document/771073");
        book.setFormatInformation("epub");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann, 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M. (2020). *My fantastic ebook title* [epub]. https://doi.org/10.1109/5.771073", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Mustermann (2020)", directInTextCitation);
    }

    @Test
    public void simpleEbookTestWithPublisher() throws ReferenceBuildException, UnsupportedSourceTypeException {
        EBook book = new EBook("id", SINGLE_AUTHOR, "My fantastic ebook title", 2020);
        book.setDoi("10.1109/5.771073");
        book.setUrl("https://ieeexplore.ieee.org/document/771073");
        book.setFormatInformation("epub");

        book.setCity("Munich");
        book.setCountry("Germany");
        book.setPublisher("Thaw");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann, 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M. (2020). *My fantastic ebook title* [epub]. Munich, Germany: Thaw. https://doi.org/10.1109/5.771073", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Mustermann (2020)", directInTextCitation);
    }

    @Test
    public void simpleEbookTestWithoutDOI() throws ReferenceBuildException, UnsupportedSourceTypeException {
        EBook book = new EBook("id", SINGLE_AUTHOR, "My fantastic ebook title", 2020);
        book.setUrl("https://ieeexplore.ieee.org/document/771073");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann, 2020)", inTextCitation);
        Assertions.assertEquals("Mustermann, M. (2020). *My fantastic ebook title*. https://ieeexplore.ieee.org/document/771073", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true)));

        Assertions.assertEquals("Mustermann (2020)", directInTextCitation);
    }

    @Test
    public void complexEbookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        EBook book = new EBook("id", COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setEdition("2nd Edition");
        book.setCity("Munich");
        book.setCountry("Germany");
        book.setPublisher("Thaw");
        book.setDoi("10.1109/5.771073");
        book.setFormatInformation("Kindle");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, false, "para. 5")));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann et al., 2020, para. 5)", inTextCitation);
        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title* [Kindle]. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor) (2nd Edition). Munich, Germany: Thaw. https://doi.org/10.1109/5.771073", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true, "para. 5")));

        Assertions.assertEquals("Mustermann et al. (2020, para. 5)", directInTextCitation);
    }

    @Test
    public void complexOnlineBookTest1() throws ReferenceBuildException, UnsupportedSourceTypeException {
        OnlineBook book = new OnlineBook("id", COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setEdition("2nd Edition");
        book.setCity("Munich");
        book.setCountry("Germany");
        book.setPublisher("Thaw");
        book.setDoi("10.1109/5.771073");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, false, "para. 5")));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann et al., 2020, para. 5)", inTextCitation);
        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor) (2nd Edition). Munich, Germany: Thaw. https://doi.org/10.1109/5.771073", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true, "para. 5")));

        Assertions.assertEquals("Mustermann et al. (2020, para. 5)", directInTextCitation);
    }

    @Test
    public void complexOnlineBookTest2() throws ReferenceBuildException, UnsupportedSourceTypeException {
        OnlineBook book = new OnlineBook("id", COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setUrl("https://ieeexplore.ieee.org/document/771073");

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, false, "para. 5")));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Mustermann et al., 2020, para. 5)", inTextCitation);
        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor). https://ieeexplore.ieee.org/document/771073", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(book, true, "para. 5")));

        Assertions.assertEquals("Mustermann et al. (2020, para. 5)", directInTextCitation);
    }

    @Test
    public void simpleArticleTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Article article = new Article(
                "id",
                Collections.singletonList(new Author(new NamedContributor("N.", "Paskin"))),
                1999,
                "Toward unique identifiers",
                "Proceedings of the IEEE",
                87,
                7,
                "S. 1208 - 1227",
                "10.1109/5.771073"
        );

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(article, false, "p. 23")));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Paskin, 1999, p. 23)", inTextCitation);
        Assertions.assertEquals("Paskin, N. (1999). Toward unique identifiers. *Proceedings of the IEEE*, 87(7), S. 1208 - 1227. 10.1109/5.771073", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(article, true, "p. 23")));

        Assertions.assertEquals("Paskin (1999, p. 23)", directInTextCitation);
    }

    @Test
    public void simpleWebsiteTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2020);
        calendar.set(Calendar.MONTH, 7);
        calendar.set(Calendar.DATE, 19);
        Date retrievalDate = calendar.getTime();

        Website website = new Website("id", "My fancy website title", "https://www.example.com", retrievalDate);

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(website, false)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(\"My fancy website title\", n. d.)", inTextCitation);
        Assertions.assertEquals("My fancy website title. (n. d.). Retrieved 2020-08-19 from https://www.example.com", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(website, true)));

        Assertions.assertEquals("\"My fancy website title\" (n. d.)", directInTextCitation);
    }

    @Test
    public void complexWebsiteTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.YEAR, 2020);
        calendar.set(Calendar.MONTH, 7);
        calendar.set(Calendar.DATE, 19);
        Date retrievalDate = calendar.getTime();

        calendar.set(Calendar.YEAR, 2020);
        calendar.set(Calendar.MONTH, 6);
        calendar.set(Calendar.DATE, 20);
        Date publicationDate = calendar.getTime();

        Website website = new Website("id", "My fancy website title", "https://www.example.com", retrievalDate);
        website.setPublicationDate(publicationDate);
        website.setAuthors(Collections.singletonList(new Author(new NamedContributor("Benjamin", "Eder"))));

        String inTextCitation = apa.addCitation(Collections.singletonList(new Citation(website, false)));
        String referenceListEntry = apa.getReferenceListEntries().get(0);

        Assertions.assertEquals("(Eder, 2020)", inTextCitation);
        Assertions.assertEquals("Eder, B. (2020-07-20). My fancy website title. Retrieved 2020-08-19 from https://www.example.com", referenceListEntry);

        String directInTextCitation = apa.addCitation(Collections.singletonList(new Citation(website, true)));

        Assertions.assertEquals("Eder (2020)", directInTextCitation);
    }

}
