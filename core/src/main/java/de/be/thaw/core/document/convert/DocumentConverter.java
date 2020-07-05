package de.be.thaw.core.document.convert;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;

/**
 * Converter of the document in another format.
 *
 * @param <T> type to convert the document to
 */
public interface DocumentConverter<T> {

    /**
     * Convert the passed document into another format.
     *
     * @param document to convert
     * @return the converted document
     * @throws DocumentConversionException in case the document could not be converted properly
     */
    T convert(Document document) throws DocumentConversionException;

}
