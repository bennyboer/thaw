package de.be.thaw.core.document.builder.impl.thingy.impl;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.reference.impl.ExternalReference;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.Set;

/**
 * Handler dealing with hyper reference thingies.
 */
public class HyperRefHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("HREF");
    }

    @Override
    public void handle(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException {
        // Add external reference to the reference model
        if (thingyNode.getArguments().isEmpty()) {
            // Expected a target URL
            throw new MissingReferenceTargetException(String.format(
                    "Expected #HREF# Thingy to include a reference target URL as first argument at %s",
                    thingyNode.getTextPosition()
            ));
        }

        String targetURL = thingyNode.getArguments().iterator().next();

        String displayName = thingyNode.getOptions().get("name");
        if (displayName != null) {
            ctx.getReferenceModel().addReference(new ExternalReference(targetURL, documentNode.getId(), displayName));
        } else {
            ctx.getReferenceModel().addReference(new ExternalReference(targetURL, documentNode.getId()));
        }
    }

}
