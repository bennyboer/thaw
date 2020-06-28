package de.be.thaw.export;

import de.be.thaw.core.document.Document;
import de.be.thaw.export.exception.ExportException;

/**
 * Exporter for a thaw document.
 */
public interface Exporter {

    /**
     * Export the passed document.
     *
     * @param document to export
     * @throws ExportException in case the export goes wrong
     */
    void export(Document document) throws ExportException;

}
