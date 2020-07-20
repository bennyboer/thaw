package de.be.thaw.core.document.builder.impl.thingy.impl;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.source.Source;
import de.be.thaw.reference.citation.styles.exception.ReferenceBuildException;
import de.be.thaw.reference.citation.styles.exception.UnsupportedSourceTypeException;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Handler dealing with the cite thingy.
 */
public class CiteHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("CITE");
    }

    @Override
    public void handle(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException {
        if (thingyNode.getArguments().isEmpty()) {
            // Expected one or more source identifier strings
            throw new MissingReferenceTargetException(String.format(
                    "Expected #CITE# Thingy at %s to include one or more identifiers for previously added sources",
                    thingyNode.getTextPosition()
            ));
        }

        boolean isDirect = Boolean.parseBoolean(thingyNode.getOptions().get("direct"));

        // Collect citations
        List<Citation> citations = new ArrayList<>();
        for (String identifier : thingyNode.getArguments()) {
            String[] parts = identifier.split(":");

            String id;
            String position = null;
            if (parts.length == 1) {
                id = identifier.trim();
            } else {
                id = parts[0].trim();
                position = parts[1].trim();
            }

            Optional<Source> sourceOptional = ctx.getSourceModel().getSource(id);
            if (sourceOptional.isEmpty()) {
                throw new DocumentBuildException(String.format(
                        "#CITE# Thingy at %s is referencing source with identifier '%s' which does not exist in the source model yet",
                        thingyNode.getTextPosition(),
                        identifier
                ));
            }

            Source source = sourceOptional.get();

            citations.add(new Citation(source, isDirect, position));
        }

        // Add found citations
        String inTextCitation;
        try {
            inTextCitation = ctx.getSourceModel().getStyle().addCitation(citations);
        } catch (UnsupportedSourceTypeException | ReferenceBuildException e) {
            throw new DocumentBuildException(e);
        }

        // Add document node representing the in-text-citation
        DocumentNode inTextCitationNode = new DocumentNode(
                new TextNode(inTextCitation, null),
                documentNode.getParent(),
                new DocumentNodeStyle(documentNode.getStyle(), new HashMap<>())
        );

        ctx.getPotentialReferences().add(new DocumentBuildContext.PotentialInternalReference(
                inTextCitationNode.getId(),
                citations.get(0).getSource().getIdentifier(), // Use first citations source as internal reference target
                null,
                null
        ));
    }

}
