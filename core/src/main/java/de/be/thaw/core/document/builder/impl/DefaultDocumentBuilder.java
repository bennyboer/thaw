package de.be.thaw.core.document.builder.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.DocumentBuilder;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.source.DocumentBuildSource;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.reference.impl.DefaultReferenceModel;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.RootNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

/**
 * Builder building a document from the provided source.
 */
public class DefaultDocumentBuilder implements DocumentBuilder<DocumentBuildSource> {

    @Override
    public Document build(DocumentBuildSource source) throws DocumentBuildException {
        ReferenceModel referenceModel = new DefaultReferenceModel();
        DocumentBuildContext ctx = new DocumentBuildContext(source.getInfo(), source.getTextModel(), referenceModel, source.getStyleModel());

        Document document = new Document(source.getInfo(), toRootNode(ctx), referenceModel);

        processPotentialReferences(document, ctx);

        return document;
    }

    /**
     * Process all dangling potential references.
     *
     * @param document of the references
     * @param ctx      the build context
     * @throws MissingReferenceTargetException in case a references target is not found
     */
    private void processPotentialReferences(Document document, DocumentBuildContext ctx) throws MissingReferenceTargetException {
        // Add potential targets to reference model if the target has been found, otherwise throw an exception
        for (DocumentBuildContext.PotentialInternalReference potentialReference : ctx.getPotentialReferences()) {
            String targetID = ctx.getLabelToNodeID().get(potentialReference.getTargetLabel());
            if (targetID == null) {
                throw new MissingReferenceTargetException(String.format(
                        "Reference target with label '%s' is missing",
                        potentialReference.getTargetLabel()
                ));
            }

            String counterName = potentialReference.getCounterName();
            if (counterName == null) {
                DocumentNode targetNode = document.getNodeForId(targetID).orElseThrow();
                ThingyNode thingyNode = (ThingyNode) targetNode.getTextNode();
                counterName = thingyNode.getName();
            }

            ctx.getReferenceModel().addReference(new InternalReference(
                    potentialReference.getSourceID(),
                    targetID,
                    counterName.toLowerCase(),
                    potentialReference.getPrefix()
            ));
        }
    }

    /**
     * Convert the passed models to a document node.
     *
     * @param ctx the document build context
     * @return the root document node
     */
    private DocumentNode toRootNode(DocumentBuildContext ctx) throws DocumentBuildException {
        return processRootNode(
                ctx.getTextModel().getRoot(),
                new DocumentNodeStyle(null, ctx.getStyleModel().getBlock("DOCUMENT").orElseThrow().getStyles()),
                ctx
        );
    }

    /**
     * Convert to a root node.
     *
     * @param node            to convert
     * @param parentStyleNode the parent style node
     * @param ctx             the build context
     * @return the root document node
     */
    private DocumentNode processRootNode(RootNode node, DocumentNodeStyle parentStyleNode, DocumentBuildContext ctx) throws DocumentBuildException {
        DocumentNode documentNode = new DocumentNode(node, null, parentStyleNode);

        if (node.hasChildren()) {
            for (Node child : node.children()) {
                ctx.processBoxNode((BoxNode) child, documentNode, parentStyleNode);
            }
        }

        return documentNode;
    }

}
