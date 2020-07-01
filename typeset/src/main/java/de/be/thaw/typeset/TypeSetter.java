package de.be.thaw.typeset;

import de.be.thaw.core.document.Document;
import de.be.thaw.typeset.page.Page;

import java.util.List;

/**
 * A typesetter setting a document on a list of pages.
 */
public interface TypeSetter {

    /**
     * Typeset the passed document to pages.
     *
     * @param document to typeset
     * @return a list of typeset pages
     */
    List<Page> typeset(Document document);

}
