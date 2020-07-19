package de.be.thaw.reference.citation.source.impl.book;

import de.be.thaw.reference.citation.source.SourceType;
import de.be.thaw.reference.citation.source.contributor.Contributor;

import java.util.List;

/**
 * A e-book source.
 */
public class EBook extends OnlineBook {

    /**
     * Information about the e-book format (Kindle, epub, ...).
     */
    private String formatInformation;

    public EBook(String identifier, List<Contributor> contributors, String title, Integer year) {
        super(identifier, contributors, title, year);
    }

    public String getFormatInformation() {
        return formatInformation;
    }

    public void setFormatInformation(String formatInformation) {
        this.formatInformation = formatInformation;
    }

    @Override
    public SourceType getType() {
        return SourceType.EBOOK;
    }

}
