package de.be.thaw.typeset.knuthplass.converter.thingyhandler;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;

import java.util.Set;

/**
 * Handler dealing with thingies in the converter.
 */
public interface ThingyHandler {

    /**
     * Get the name of the thingy this handler is able to process.
     *
     * @return thingy name
     */
    Set<String> getThingyNames();

    /**
     * Handle the passed thingy node.
     *
     * @param node         to handle
     * @param documentNode the original node in the document
     * @param ctx          the conversion context
     * @throws DocumentConversionException in case the thingy could not be handled properly here
     */
    void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException;

}
