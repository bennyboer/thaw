package de.be.thaw.typeset.knuthplass.paragraph.handler;

import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.TypeSettingContext;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;

/**
 * Handler dealing with typesetting a specific paragraph.
 */
public interface ParagraphTypesetHandler {

    /**
     * Get the paragraph type this handler supports.
     *
     * @return type
     */
    ParagraphType supportedType();

    /**
     * Handle the passed paragraph.
     *
     * @param paragraph to handle
     * @param ctx       the type setting context
     * @throws TypeSettingException in case the paragraph could not be typeset properly
     */
    void handle(Paragraph paragraph, TypeSettingContext ctx) throws TypeSettingException;

}
