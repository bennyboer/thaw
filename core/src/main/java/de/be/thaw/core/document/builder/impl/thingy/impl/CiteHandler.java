package de.be.thaw.core.document.builder.impl.thingy.impl;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.reference.citation.Citation;
import de.be.thaw.reference.citation.SourceCitation;
import de.be.thaw.reference.citation.exception.CouldNotLoadBibliographyException;
import de.be.thaw.reference.citation.exception.MissingSourceException;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        // Collect citations
        List<Citation> citations = new ArrayList<>();
        for (String identifier : thingyNode.getArguments()) {
            String[] parts = identifier.split(",");

            if (parts.length == 0) {
                throw new DocumentBuildException(String.format(
                        "#CITE# Thingy at %s needs at least the ID of the source to cite as argument. Example: #CITE, 'MySourceID, page 42'#",
                        thingyNode.getTextPosition()
                ));
            }
            SourceCitation citation = new SourceCitation(parts[0].trim());

            // Parse additional options
            if (parts.length > 1) {
                for (int i = 1; i < parts.length; i++) {
                    String part = parts[i];

                    String[] keyValuePair = part.split("=");
                    String key = keyValuePair[0].trim();
                    if (keyValuePair.length == 1) {
                        // Only key specified -> is boolean key-value-pair
                        switch (key) {
                            case "author-only" -> citation.setAuthorOnly(true);
                            case "suppress-author" -> citation.setSuppressAuthor(true);
                            case "near-note" -> citation.setNearNote(true);
                        }
                    } else {
                        String value = keyValuePair[1].trim();

                        switch (key) {
                            case "author-only" -> citation.setAuthorOnly(Boolean.parseBoolean(value));
                            case "suppress-author" -> citation.setSuppressAuthor(Boolean.parseBoolean(value));
                            case "near-note" -> citation.setNearNote(Boolean.parseBoolean(value));
                            case "prefix" -> citation.setPrefix(value);
                            case "suffix" -> citation.setSuffix(value);
                            case "location" -> {
                                // Parse label (chapter, page, paragraph, ...) and locator (the actual page number, etc.)
                                String[] parts2 = value.split(" ");

                                citation.setLabel(parts2[0].trim());
                                citation.setLocator(parts2[1].trim());
                            }
                        }
                    }
                }
            }

            // Check if source with sourceID is specified in bibliography
            if (!ctx.getCitationManager().hasSource(citation.getSourceID())) {
                throw new DocumentBuildException(String.format(
                        "#CITE# Thingy at %s is referencing source with identifier '%s' which does not exist in the provided bibliography yet",
                        thingyNode.getTextPosition(),
                        identifier
                ));
            }

            citations.add(citation);
        }

        // Register found citations
        String inTextCitation;
        try {
            inTextCitation = ctx.getCitationManager().register(citations);
        } catch (MissingSourceException | CouldNotLoadBibliographyException e) {
            throw new DocumentBuildException(e); // Should not happen since this case should be caught above already
        }

        // Add document node representing the in-text-citation
        DocumentNode inTextCitationNode = new DocumentNode(
                new TextNode(inTextCitation, null),
                documentNode.getParent(),
                new DocumentNodeStyle(documentNode.getStyle(), new HashMap<>())
        );

        ctx.getPotentialReferences().add(new DocumentBuildContext.PotentialInternalReference(
                inTextCitationNode.getId(),
                citations.get(0).getSourceID(), // Use first citations source as internal reference target
                null,
                true
        ));
    }

}
