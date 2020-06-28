package de.be.thaw.export;

import de.be.thaw.core.document.Document;
import de.be.thaw.export.exception.ExportException;

import java.nio.file.Path;

/**
 * Exporter for a thaw document.
 */
public interface Exporter {

    /**
     * Export the passed document.
     *
     * @param document to export
     * @param path     to save the resulting file to
     * @throws ExportException in case the export goes wrong
     */
    void export(Document document, Path path) throws ExportException;

}
