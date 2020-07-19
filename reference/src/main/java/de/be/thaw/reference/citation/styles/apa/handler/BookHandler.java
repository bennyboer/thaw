package de.be.thaw.reference.citation.styles.apa.handler;

import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.contributor.Author;
import de.be.thaw.reference.citation.source.contributor.OtherContributor;
import de.be.thaw.reference.citation.source.impl.book.Book;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handler for book sources.
 */
public class BookHandler extends AbstractAPAHandler {

    @Override
    public Set<SourceType> supports() {
        return Set.of(SourceType.BOOK);
    }

    @Override
    public String buildReferenceListEntry(Source source) {
        Book book = (Book) source;

        List<Author> authors = book.getContributors().stream()
                .filter(c -> c instanceof Author)
                .map(c -> (Author) c)
                .collect(Collectors.toList());
        List<OtherContributor> otherContributors = book.getContributors().stream()
                .filter(c -> c instanceof OtherContributor)
                .map(c -> (OtherContributor) c)
                .collect(Collectors.toList());

        String authorStr = authorListToString(authors, false, false);
        String contributorStr = otherContributorListToString(otherContributors);

        contributorStr = !contributorStr.isEmpty() ? " " + contributorStr : "";
        String edition = book.getEdition() != null ? String.format(" (%s)", book.getEdition()) : "";
        String additonalInfo = !contributorStr.isEmpty() || !edition.isEmpty() ? contributorStr + edition + "." : "";

        String location = "";
        if (book.getCity() != null) {
            location += " " + book.getCity();
        }
        if (book.getCountry() != null) {
            if (!location.isEmpty()) {
                location += ", ";
            } else {
                location += " ";
            }

            location += book.getCountry();
        }

        if (book.getPublisher() != null) {
            if (!location.isEmpty()) {
                location += ": ";
            } else {
                location += " ";
            }

            location += book.getPublisher();
        }

        // Last name, Initials. (Year). Book title. (Contributor initials, last name, role.) (Edition). City, State/Country: Publisher.
        return String.format(
                "%s%s (%d). *%s*.%s%s",
                authorStr,
                authorStr.endsWith(".") ? "" : ".",
                book.getYear(),
                book.getTitle(),
                additonalInfo,
                location
        );
    }

    @Override
    public String getCitePrefix(Citation citation) {
        return authorListToString(((Book) citation.getSource()).getContributors().stream()
                .filter(c -> c instanceof Author)
                .map(c -> (Author) c)
                .collect(Collectors.toList()), true, citation.isDirect());
    }

    @Override
    public Optional<String> getCitePosition(Citation citation) {
        return Optional.ofNullable(citation.getPosition());
    }

    @Override
    public Optional<Integer> getCiteYear(Citation citation) {
        return Optional.ofNullable(((Book) citation.getSource()).getYear());
    }

}
