package de.be.thaw.export;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.typeset.page.Page;

import java.nio.file.Path;
import java.util.List;

/**
 * Exporter for a typeset thaw document.
 */
public interface Exporter {

    /**
     * Export the passed pages.
     *
     * @param pages to export
     * @param path  to save the resulting file to
     * @throws ExportException in case the export goes wrong
     */
    void export(List<Page> pages, Path path) throws ExportException;

}
