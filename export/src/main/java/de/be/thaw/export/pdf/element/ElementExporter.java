package de.be.thaw.export.pdf.element;

import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.ExportContext;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.ElementType;

import java.util.Set;

/**
 * Dealing with exporting a typeset element of a page.
 */
public interface ElementExporter {

    /**
     * Check the supported element types to be exported using
     * this exporter.
     *
     * @return supported element types
     */
    Set<ElementType> supportedElementTypes();

    /**
     * Export the passed element.
     *
     * @param element to export
     * @param ctx     current exporting context
     * @throws ExportException in case the element could not be exported properly
     */
    void export(Element element, ExportContext ctx) throws ExportException;

}
