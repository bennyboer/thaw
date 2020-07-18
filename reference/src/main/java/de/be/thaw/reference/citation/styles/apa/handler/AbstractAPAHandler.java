package de.be.thaw.reference.citation.styles.apa.handler;

import de.be.thaw.reference.citation.source.contributor.Author;
import de.be.thaw.reference.citation.source.contributor.NamedContributor;
import de.be.thaw.reference.citation.source.contributor.Organisation;
import de.be.thaw.reference.citation.source.contributor.OtherContributor;
import de.be.thaw.reference.citation.styles.SourceHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * An abstract source handler for the APA citation style.
 */
public abstract class AbstractAPAHandler implements SourceHandler {

    /**
     * Convert the passed other contributors to string.
     *
     * @param otherContributors to convert
     * @return the converted other contributors
     */
    public String otherContributorListToString(List<OtherContributor> otherContributors) {
        if (otherContributors.isEmpty()) {
            return "";
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append('(');

        int i = 0;
        for (OtherContributor otherContributor : otherContributors) {
            if (i != 0) {
                // Is not the first one
                buffer.append(", ");
            }

            if (otherContributor.getContributor() instanceof Organisation) {
                buffer.append(((Organisation) otherContributor.getContributor()).getName());
            } else if (otherContributor.getContributor() instanceof NamedContributor) {
                NamedContributor namedContributor = (NamedContributor) otherContributor.getContributor();

                buffer.append(namedContributor.getFirstName().charAt(0));
                buffer.append(". ");
                buffer.append(namedContributor.getLastName());
            } else {
                throw new UnsupportedOperationException(String.format(
                        "Contributor type '%s' unknown",
                        otherContributor.getContributor().getClass().getSimpleName()
                ));
            }

            if (otherContributor.getRole() != null) {
                buffer.append(", ");
                buffer.append(otherContributor.getRole());
            }

            i++;
        }

        buffer.append(')');
        return buffer.toString();
    }

    /**
     * Convert the passed authors list to a string.
     *
     * @param authors to convert
     * @param inText  whether to convert for in-text-citation or reference list entry
     * @return the converted authors list
     */
    public String authorListToString(List<Author> authors, boolean inText) {
        List<String> authorEntries = authors.stream()
                .map(a -> getAPAStyleAuthorName(a, inText))
                .collect(Collectors.toCollection(ArrayList::new));

        if (authorEntries.size() == 1) {
            return authorEntries.get(0);
        } else {
            if (inText && authorEntries.size() > 3) {
                return String.format("%s et al.", authorEntries.get(0));
            } else {
                StringBuilder buffer = new StringBuilder();

                for (int i = 0; i < authorEntries.size(); i++) {
                    String authorName = authorEntries.get(i);

                    if (i == authorEntries.size() - 1) {
                        // Is the last entry
                        buffer.append(" & ");
                    } else if (i != 0) {
                        // Is not the first entry
                        buffer.append(", ");
                    }

                    buffer.append(authorName);
                }

                return buffer.toString();
            }
        }
    }

    /**
     * Get the author name of the passed author.
     *
     * @param author to convert
     * @param inText whether we are dealing with in-text-citations or a reference list entry
     * @return the converted author name in APA style
     */
    private String getAPAStyleAuthorName(Author author, boolean inText) {
        if (author.getContributor() instanceof Organisation) {
            return ((Organisation) author.getContributor()).getName();
        } else if (author.getContributor() instanceof NamedContributor) {
            NamedContributor namedContributor = (NamedContributor) author.getContributor();

            if (inText) {
                return namedContributor.getLastName();
            } else {
                return String.format(
                        "%s, %s.",
                        namedContributor.getLastName(),
                        String.valueOf(namedContributor.getFirstName().charAt(0)).toUpperCase()
                );
            }
        } else {
            throw new UnsupportedOperationException(String.format(
                    "Contributor type '%s' unknown",
                    author.getContributor().getClass().getSimpleName()
            ));
        }
    }

}
