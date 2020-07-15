package de.be.thaw.core.document.builder.impl.thingy;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.Set;

/**
 * Handler dealing with thingies during document building.
 */
public interface ThingyHandler {

    /**
     * Get the names of the thingies this handler is able to deal with.
     *
     * @return thingy names
     */
    Set<String> getThingyNames();

    /**
     * Handle the passed thingy node.
     *
     * @param thingyNode   thingy node to handle
     * @param documentNode the document node of the thingy
     * @param ctx          the build context
     * @throws DocumentBuildException in case the handler encounters a problem
     */
    void handle(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException;

}
