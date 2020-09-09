package de.be.thaw.core.document.builder.impl.thingy.impl;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.Set;

/**
 * Handler dealing with reference thingies.
 */
public class RefHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("REF");
    }

    @Override
    public void handle(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException {
        // Add potential internal reference
        if (thingyNode.getArguments().isEmpty()) {
            // Expected a target label
            throw new MissingReferenceTargetException(String.format(
                    "Expected #REF# Thingy to include a reference target label as first argument at %s",
                    thingyNode.getTextPosition()
            ));
        }

        String targetLabel = thingyNode.getArguments().iterator().next();
        ctx.getPotentialReferences().add(new DocumentBuildContext.PotentialInternalReference(
                documentNode.getId(),
                targetLabel,
                thingyNode.getOptions().get("prefix")
        ));
    }

}

