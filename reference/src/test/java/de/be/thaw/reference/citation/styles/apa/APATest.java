package de.be.thaw.reference.citation.styles.apa;

import de.be.thaw.reference.citation.source.contributor.Author;
import de.be.thaw.reference.citation.source.contributor.Contributor;
import de.be.thaw.reference.citation.source.contributor.NamedContributor;
import de.be.thaw.reference.citation.source.contributor.Organisation;
import de.be.thaw.reference.citation.source.contributor.OtherContributor;
import de.be.thaw.reference.citation.source.impl.book.Book;
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

}
