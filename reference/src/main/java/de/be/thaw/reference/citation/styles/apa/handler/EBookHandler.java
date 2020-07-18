package de.be.thaw.reference.citation.styles.apa.handler;

import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.contributor.Author;
import de.be.thaw.reference.citation.source.contributor.OtherContributor;
import de.be.thaw.reference.citation.source.impl.book.EBook;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Handler for ebook sources.
 */
public class EBookHandler extends BookHandler {

    @Override
    public Set<SourceType> supports() {
        return Set.of(SourceType.EBOOK);
    }

    @Override
    public String buildReferenceListEntry(Source source) {
        EBook book = (EBook) source;

        List<Author> authors = book.getContributors().stream()
                .filter(c -> c instanceof Author)
                .map(c -> (Author) c)
                .collect(Collectors.toList());
        List<OtherContributor> otherContributors = book.getContributors().stream()
                .filter(c -> c instanceof OtherContributor)
                .map(c -> (OtherContributor) c)
                .collect(Collectors.toList());

        String authorStr = authorListToString(authors, false);
        String contributorStr = otherContributorListToString(otherContributors);

        contributorStr = !contributorStr.isEmpty() ? " " + contributorStr : "";
        String edition = book.getEdition() != null ? String.format(" (%s)", book.getEdition()) : "";
        String additonalInfo = !contributorStr.isEmpty() || !edition.isEmpty() ? contributorStr + edition + "." : "";

        String formatInformation = "";
        if (book.getFormatInformation() != null) {
            formatInformation = String.format(" [%s]", book.getFormatInformation());
        }

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

        if (book.getDoi() != null) {
            String doi = book.getDoi();
            if (!doi.startsWith("http")) {
                doi = "https://doi.org/" + doi;
            }

            if (location.isEmpty()) {
                location += " ";
            } else {
                location += ". ";
            }

            location += doi;
        } else if (book.getUrl() != null) {
            if (location.isEmpty()) {
                location += " ";
            } else {
                location += ". ";
            }

            location += book.getUrl();
        }

        // Last name, Initials. (Year). Book title. (Contributor initials, last name, role.) (Edition). City, State/Country: Publisher.
        return String.format(
                "%s%s (%d). *%s*%s.%s%s",
                authorStr,
                authorStr.endsWith(".") ? "" : ".",
                book.getYear(),
                book.getTitle(),
                formatInformation,
                additonalInfo,
                location
        );
    }

}
