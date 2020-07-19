package de.be.thaw.reference.citation.styles.apa.handler;

import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.impl.Website;

import java.util.Calendar;
import java.util.Optional;
import java.util.Set;

/**
 * Handler for website sources.
 */
public class WebsiteHandler extends AbstractAPAHandler {

    @Override
    public Set<SourceType> supports() {
        return Set.of(SourceType.WEBSITE);
    }

    @Override
    public String buildReferenceListEntry(Source source) {
        Website website = (Website) source;

        String date;
        if (website.getPublicationDate() != null) {
            date = String.format(
                    "(%s)",
                    getSettings().getDateFormat().format(website.getPublicationDate())
            );
        } else {
            date = String.format("(%s)", getSettings().getNoDateStr());
        }

        String prefix;
        if (website.getAuthors().isEmpty()) {
            prefix = String.format("%s. %s", website.getTitle(), date);
        } else {
            String authorStr = authorListToString(website.getAuthors(), false, false);
            prefix = String.format(
                    "%s%s %s. %s",
                    authorStr,
                    authorStr.endsWith(".") ? "" : ".",
                    date,
                    website.getTitle()
            );
        }

        String retrieved;
        if (website.getRetrievalDate() != null) {
            retrieved = String.format(
                    "Retrieved %s from %s",
                    getSettings().getDateFormat().format(website.getRetrievalDate()),
                    website.getUrl()
            );
        } else {
            retrieved = website.getUrl();
        }

        return String.format(
                "%s. %s",
                prefix,
                retrieved
        );
    }

    @Override
    public String getCitePrefix(Citation citation) {
        Website website = (Website) citation.getSource();

        if (website.getAuthors().isEmpty()) {
            return String.format("%s%s%s", getSettings().getStartQuotationMark(), website.getTitle(), getSettings().getEndQuotationMark());
        } else {
            return authorListToString(((Website) citation.getSource()).getAuthors(), true, citation.isDirect());
        }
    }

    @Override
    public Optional<String> getCitePosition(Citation citation) {
        return Optional.ofNullable(citation.getPosition());
    }

    @Override
    public Optional<Integer> getCiteYear(Citation citation) {
        Website website = (Website) citation.getSource();

        if (website.getPublicationDate() == null) {
            return Optional.empty();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(website.getPublicationDate());

        return Optional.of(calendar.get(Calendar.YEAR));
    }

}
