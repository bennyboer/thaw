package de.be.thaw.reference.citation.styles.apa.handler;

import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.source.contributor.Author;
import de.be.thaw.reference.citation.source.contributor.NamedContributor;
import de.be.thaw.reference.citation.source.contributor.Organisation;
import de.be.thaw.reference.citation.source.contributor.OtherContributor;
import de.be.thaw.reference.citation.styles.SourceHandler;
import de.be.thaw.reference.citation.styles.apa.APASettings;
import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * An abstract source handler for the APA citation style.
 */
public abstract class AbstractAPAHandler implements SourceHandler {

    /**
     * Settings for the APA citation style.
     */
    private APASettings settings;

    /**
     * Get the APA citation style settings.
     *
     * @return citation style settings
     */
    public APASettings getSettings() {
        return settings;
    }

    /**
     * Set the APA citation style settings.
     *
     * @param settings to set
     */
    public void setSettings(APASettings settings) {
        this.settings = settings;
    }

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
     * @param direct  whether we have a direct citation
     * @return the converted authors list
     */
    public String authorListToString(List<Author> authors, boolean inText, boolean direct) {
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
                        buffer.append(direct ? String.format(" %s ", getSettings().getAndStr()) : " & ");
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

    @Override
    public String buildInTextCitation(Citation citation) throws ReferenceBuildException {
        String prefix = getCitePrefix(citation);
        String year = getCiteYear(citation).map(String::valueOf).orElse(getSettings().getNoDateStr());
        String position = getCitePosition(citation).map(p -> String.format(", %s", p)).orElse("");

        if (citation.isDirect()) {
            return String.format("%s (%s%s)", prefix, year, position);
        } else {
            return String.format("%s, %s%s", prefix, year, position);
        }
    }

    /**
     * Get the citation prefix.
     *
     * @param citation to get prefix from
     * @return citation prefix
     */
    public abstract String getCitePrefix(Citation citation);

    /**
     * Get the position of the passed citation (if any).
     *
     * @param citation to get position of
     * @return position or empty optional
     */
    public abstract Optional<String> getCitePosition(Citation citation);

    /**
     * Get the year for the passed citation.
     *
     * @param citation to get year from
     * @return year or an empty optional
     */
    public abstract Optional<Integer> getCiteYear(Citation citation);

}
