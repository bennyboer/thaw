package de.be.thaw.core.document.builder.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.DocumentBuilder;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.source.DocumentBuildSource;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.node.style.DocumentNodeStyle;
import de.be.thaw.core.document.util.PageRange;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.shared.ThawContext;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.HeaderFooterStyle;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.RootNode;

import java.io.File;
import java.util.Optional;

/**
 * Builder building a document from the provided source.
 */
public class DefaultDocumentBuilder implements DocumentBuilder<DocumentBuildSource> {

    @Override
    public Document build(DocumentBuildSource source) throws DocumentBuildException {
        ReferenceModel referenceModel = source.getReferenceModel();
        DocumentBuildContext ctx = new DocumentBuildContext(source.getInfo(), source.getTextModel(), referenceModel, source.getStyleModel(), source.getSourceModel());
        if (source.getParentDocument() != null) {
            ctx.setParentDocument(source.getParentDocument());
        }

        DocumentNode root = toRootNode(ctx);
        loadHeadersAndFooters(root, ctx);

        Document document = new Document(
                source.getInfo(),
                root,
                referenceModel,
                ctx.getSourceModel(),
                source.getStyleModel(),
                ctx.getHeaderNodes(),
                ctx.getFooterNodes(),
                ctx.getFootNotes(),
                source.getParentDocument() != null ? source.getParentDocument().getNodeLookup() : null
        );

        processPotentialReferences(document, ctx);

        return document;
    }

    /**
     * Load all headers and footers.
     *
     * @param root the root node
     * @param ctx  the document build context
     */
    private void loadHeadersAndFooters(DocumentNode root, DocumentBuildContext ctx) throws DocumentBuildException {
        Optional<HeaderFooterStyle> optionalStyle = root.getStyle().getStyleAttribute(
                StyleType.HEADER_FOOTER,
                s -> Optional.ofNullable((HeaderFooterStyle) s)
        );

        if (optionalStyle.isEmpty()) {
            return;
        }

        HeaderFooterStyle style = optionalStyle.get();

        if (style.getHeader() != null) {
            HeaderFooterStyle.HeaderFooterSettings settings = style.getHeader();
            File headerFile = new File(ThawContext.getInstance().getCurrentFolder(), settings.getDefaultSrc());

            ctx.getHeaderNodes().put(null, ctx.loadHeaderFooterNode(headerFile));

            // Add special headers
            if (settings.getSpecialSettings() != null) {
                for (HeaderFooterStyle.SpecialHeaderFooterSettings specialSettings : settings.getSpecialSettings()) {
                    headerFile = new File(ThawContext.getInstance().getCurrentFolder(), specialSettings.getSrc());

                    ctx.getHeaderNodes().put(new PageRange(specialSettings.getStartPage(), specialSettings.getEndPage()), ctx.loadHeaderFooterNode(headerFile));
                }
            }
        }

        if (style.getFooter() != null) {
            HeaderFooterStyle.HeaderFooterSettings settings = style.getFooter();
            File footerFile = new File(ThawContext.getInstance().getCurrentFolder(), settings.getDefaultSrc());

            ctx.getFooterNodes().put(null, ctx.loadHeaderFooterNode(footerFile));

            // Add special footers
            if (settings.getSpecialSettings() != null) {
                for (HeaderFooterStyle.SpecialHeaderFooterSettings specialSettings : settings.getSpecialSettings()) {
                    footerFile = new File(ThawContext.getInstance().getCurrentFolder(), specialSettings.getSrc());

                    ctx.getFooterNodes().put(new PageRange(specialSettings.getStartPage(), specialSettings.getEndPage()), ctx.loadHeaderFooterNode(footerFile));
                }
            }
        }
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
            String targetID = ctx.getReferenceModel().getNodeIDForLabel(potentialReference.getTargetLabel())
                    .orElseThrow(() -> {
                        if (potentialReference.isFromCitation()) {
                            return new MissingReferenceTargetException(String.format(
                                    "Reference list entry for label '%s' is missing. Have you forgotten to add the #REFERENCES# Thingy to your document?",
                                    potentialReference.getTargetLabel()
                            ));
                        } else {
                            return new MissingReferenceTargetException(String.format(
                                    "Reference target with label '%s' is missing",
                                    potentialReference.getTargetLabel()
                            ));
                        }
                    });

            ctx.getReferenceModel().addReference(new InternalReference(
                    potentialReference.getSourceID(),
                    targetID,
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
