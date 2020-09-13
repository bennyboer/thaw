package de.be.thaw.reference.citation;

import de.be.thaw.info.model.language.Language;
import de.be.thaw.reference.citation.csl.CSLCitationManager;
import de.be.thaw.reference.citation.empty.EmptyCitationManager;
import de.be.thaw.reference.citation.exception.CitationManagerCreationException;
import de.be.thaw.reference.citation.exception.CouldNotLoadBibliographyException;
import de.be.thaw.reference.citation.exception.UnsupportedBibliographyFormatException;
import de.be.thaw.reference.citation.exception.UnsupportedCitationStyleException;
import org.jetbrains.annotations.Nullable;

import java.io.File;

/**
 * Factory for citation managers.
 */
public class CitationManagerFactory {

    /**
     * Get the current instance of the factory.
     *
     * @return instance
     */
    public static CitationManagerFactory getInstance() {
        return InstanceHolder.INSTANCE;
    }

    /**
     * Produce a citation manager for the passed bibliography file and settings.
     *
     * @param bibliographyFile  to use
     * @param citationStyleName name of the citation style to use
     * @param language          to use for localizations
     * @return the citation manager
     */
    public CitationManager create(@Nullable File bibliographyFile, String citationStyleName, Language language) throws CitationManagerCreationException {
        if (bibliographyFile == null) {
            return new EmptyCitationManager();
        } else {
            try {
                return new CSLCitationManager(bibliographyFile, citationStyleName, language);
            } catch (UnsupportedBibliographyFormatException | CouldNotLoadBibliographyException | UnsupportedCitationStyleException e) {
                throw new CitationManagerCreationException(String.format(
                        "Citation manager could not be created for bibliography file at '%s', citation style '%s' and language '%s'. Exception was: '%s'",
                        bibliographyFile.getAbsolutePath(),
                        citationStyleName,
                        language.getCode(),
                        e.getMessage()
                ), e);
            }
        }
    }

    /**
     * Holder of the citation manager factory singleton instance.
     */
    private static final class InstanceHolder {

        /**
         * Instance of the factory.
         */
        static final CitationManagerFactory INSTANCE = new CitationManagerFactory();

    }

}
