package de.be.thaw.reference.citation.styles.apa;

import de.be.thaw.reference.citation.source.contributor.Author;
import de.be.thaw.reference.citation.source.contributor.Contributor;
import de.be.thaw.reference.citation.source.contributor.NamedContributor;
import de.be.thaw.reference.citation.source.contributor.Organisation;
import de.be.thaw.reference.citation.source.contributor.OtherContributor;
import de.be.thaw.reference.citation.source.impl.Article;
import de.be.thaw.reference.citation.source.impl.book.Book;
import de.be.thaw.reference.citation.source.impl.book.EBook;
import de.be.thaw.reference.citation.source.impl.book.OnlineBook;
import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;
import de.be.thaw.reference.citation.styles.exception.UnsupportedSourceTypeException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Collections;
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

    private static APA apa;

    @BeforeAll
    public static void setup() {
        apa = new APA();
    }

    @Test
    public void singleAuthorBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(SINGLE_AUTHOR, "My fantastic book title", 2020);

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M. (2020). *My fantastic book title*.", referenceListEntry);
    }

    @Test
    public void multipleAuthorsBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(THREE_AUTHORS, "My fantastic book title", 2020);

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M., Doe, J. & Eder, B. (2020). *My fantastic book title*.", referenceListEntry);
    }

    @Test
    public void organisationAuthorBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(SINGLE_ORGANISATION_AUTHOR, "My fantastic book title", 2020);

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Thaw Inc. (2020). *My fantastic book title*.", referenceListEntry);
    }

    @Test
    public void mixedAuthorTypeBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(MIXED_AUTHOR_TYPES, "My fantastic book title", 2020);

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Thaw Inc. & Mustermann, M. (2020). *My fantastic book title*.", referenceListEntry);
    }

    @Test
    public void aLotOfAuthorsBookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(FIVE_AUTHORS, "My fantastic book title", 2020);

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Name, A. & Disney, W. (2020). *My fantastic book title*.", referenceListEntry);
    }

    @Test
    public void complexBookTest1() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setEdition("2nd Edition");
        book.setCity("Munich");
        book.setCountry("Germany");
        book.setPublisher("Thaw");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor) (2nd Edition). Munich, Germany: Thaw", referenceListEntry);
    }

    @Test
    public void complexBookTest2() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setPublisher("Thaw");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor). Thaw", referenceListEntry);
    }

    @Test
    public void complexBookTest3() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setCountry("Germany");
        book.setPublisher("Thaw");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor). Germany: Thaw", referenceListEntry);
    }

    @Test
    public void simpleInTextBookCitation() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book = new Book(SINGLE_AUTHOR, "My fantastic book title", 2020);

        String citation = apa.buildInTextCitation(Collections.singletonList(book), Collections.singletonList(null));

        Assertions.assertEquals("Mustermann, 2020", citation);

        String citation2 = apa.buildInTextCitation(Collections.singletonList(book), Collections.singletonList("p. 43"));

        Assertions.assertEquals("Mustermann, 2020, p. 43", citation2);
    }

    @Test
    public void complexInTextBookCitation() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Book book1 = new Book(COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book1.setEdition("2nd Edition");
        book1.setCity("Munich");
        book1.setCountry("Germany");
        book1.setPublisher("Thaw");

        Book book2 = new Book(SINGLE_ORGANISATION_AUTHOR, "Another book", 1964);

        Book book3 = new Book(Collections.singletonList(new Author(new NamedContributor("Lewis", "Carroll"))), "Alice in wonderland", 2008);

        String citation = apa.buildInTextCitation(Arrays.asList(book1, book2, book3), Arrays.asList("p. 1", "p. 43", null));

        Assertions.assertEquals("Carroll, 2008; Mustermann et al., 2020, p. 1; Thaw Inc., 1964, p. 43", citation);
    }

    @Test
    public void simpleEbookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        EBook book = new EBook(SINGLE_AUTHOR, "My fantastic ebook title", 2020);
        book.setDoi("10.1109/5.771073");
        book.setUrl("https://ieeexplore.ieee.org/document/771073");
        book.setFormatInformation("epub");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M. (2020). *My fantastic ebook title* [epub]. https://doi.org/10.1109/5.771073", referenceListEntry);
    }

    @Test
    public void simpleEbookTestWithPublisher() throws ReferenceBuildException, UnsupportedSourceTypeException {
        EBook book = new EBook(SINGLE_AUTHOR, "My fantastic ebook title", 2020);
        book.setDoi("10.1109/5.771073");
        book.setUrl("https://ieeexplore.ieee.org/document/771073");
        book.setFormatInformation("epub");

        book.setCity("Munich");
        book.setCountry("Germany");
        book.setPublisher("Thaw");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M. (2020). *My fantastic ebook title* [epub]. Munich, Germany: Thaw. https://doi.org/10.1109/5.771073", referenceListEntry);
    }

    @Test
    public void simpleEbookTestWithoutDOI() throws ReferenceBuildException, UnsupportedSourceTypeException {
        EBook book = new EBook(SINGLE_AUTHOR, "My fantastic ebook title", 2020);
        book.setUrl("https://ieeexplore.ieee.org/document/771073");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M. (2020). *My fantastic ebook title*. https://ieeexplore.ieee.org/document/771073", referenceListEntry);
    }

    @Test
    public void complexEbookTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        EBook book = new EBook(COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setEdition("2nd Edition");
        book.setCity("Munich");
        book.setCountry("Germany");
        book.setPublisher("Thaw");
        book.setDoi("10.1109/5.771073");
        book.setFormatInformation("Kindle");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title* [Kindle]. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor) (2nd Edition). Munich, Germany: Thaw. https://doi.org/10.1109/5.771073", referenceListEntry);

        String citation = apa.buildInTextCitation(Collections.singletonList(book), Collections.singletonList("para. 5"));

        Assertions.assertEquals("Mustermann et al., 2020, para. 5", citation);
    }

    @Test
    public void complexOnlineBookTest1() throws ReferenceBuildException, UnsupportedSourceTypeException {
        OnlineBook book = new OnlineBook(COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setEdition("2nd Edition");
        book.setCity("Munich");
        book.setCountry("Germany");
        book.setPublisher("Thaw");
        book.setDoi("10.1109/5.771073");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor) (2nd Edition). Munich, Germany: Thaw. https://doi.org/10.1109/5.771073", referenceListEntry);

        String citation = apa.buildInTextCitation(Collections.singletonList(book), Collections.singletonList("para. 5"));

        Assertions.assertEquals("Mustermann et al., 2020, para. 5", citation);
    }

    @Test
    public void complexOnlineBookTest2() throws ReferenceBuildException, UnsupportedSourceTypeException {
        OnlineBook book = new OnlineBook(COMPLEX_CONTRIBUTORS, "My fantastic book title", 2020);
        book.setUrl("https://ieeexplore.ieee.org/document/771073");

        String referenceListEntry = apa.buildReferenceListEntry(book);

        Assertions.assertEquals("Mustermann, M., Doe, J., Eder, B., Walt Disney Enterprises & Name, A. (2020). *My fantastic book title*. (A. Name, Translator, J. Editing, Editor, W. Disney, Editor). https://ieeexplore.ieee.org/document/771073", referenceListEntry);

        String citation = apa.buildInTextCitation(Collections.singletonList(book), Collections.singletonList("para. 5"));

        Assertions.assertEquals("Mustermann et al., 2020, para. 5", citation);
    }

    @Test
    public void simpleArticleTest() throws ReferenceBuildException, UnsupportedSourceTypeException {
        Article article = new Article(
                Collections.singletonList(new Author(new NamedContributor("N.", "Paskin"))),
                1999,
                "Toward unique identifiers",
                "Proceedings of the IEEE",
                87,
                7,
                "S. 1208 - 1227",
                "10.1109/5.771073"
        );

        String referenceListEntry = apa.buildReferenceListEntry(article);

        Assertions.assertEquals("Paskin, N. (1999). Toward unique identifiers. *Proceedings of the IEEE*, 87(7), S. 1208 - 1227. 10.1109/5.771073", referenceListEntry);

        String citation = apa.buildInTextCitation(Collections.singletonList(article), Collections.singletonList("p. 23"));

        Assertions.assertEquals("Paskin, 1999, p. 23", citation);
    }

}
