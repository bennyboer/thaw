package de.be.thaw.core.document.builder.impl;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.DocumentBuilder;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.source.DocumentBuildSource;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.util.PageRange;
import de.be.thaw.reference.ReferenceModel;
import de.be.thaw.reference.impl.InternalReference;
import de.be.thaw.shared.ThawContext;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.RootNode;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Builder building a document from the provided source.
 */
public class DefaultDocumentBuilder implements DocumentBuilder<DocumentBuildSource> {

    @Override
    public Document build(DocumentBuildSource source) throws DocumentBuildException {
        ReferenceModel referenceModel = source.getReferenceModel();
        DocumentBuildContext ctx = new DocumentBuildContext(source.getInfo(), source.getTextModel(), referenceModel, source.getStyleModel());
        if (source.getParentDocument() != null) {
            ctx.setParentDocument(source.getParentDocument());
        }

        DocumentNode root = toRootNode(ctx);
        loadHeadersAndFooters(root, ctx);

        Document document = new Document(
                source.getInfo(),
                root,
                referenceModel,
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
        // Collect all header and footer definitions from the "page" style blocks
        List<StyleBlock> pageStyleBlocks = ctx.getStyleModel().getBlocks().stream().filter(b -> b.getSelector().targetName().isPresent()
                && b.getSelector().targetName().orElseThrow().equals("page")).collect(Collectors.toList());

        class HeaderFooterEntry {
            PageRange range;
            String folder;
            boolean isHeader;
        }

        List<HeaderFooterEntry> entries = new ArrayList<>();

        for (StyleBlock block : pageStyleBlocks) {
            PageRange range = null;
            if (block.getSelector().pseudoClassName().isPresent()) {
                String pseudoClassName = block.getSelector().pseudoClassName().orElseThrow();

                List<String> pseudoClassSettings = block.getSelector().pseudoClassSettings().orElse(Collections.emptyList());
                int firstSettingNr = -1;
                int secondSettingNr = -1;
                if (pseudoClassSettings.size() == 1) {
                    try {
                        firstSettingNr = Integer.parseInt(pseudoClassSettings.get(0));
                    } catch (NumberFormatException e) {
                        throw new DocumentBuildException(String.format(
                                "Could not parse pseudo class setting for the page style block pseudo class '%s'. Encountered value %s while we anticipated an integer.",
                                pseudoClassName,
                                pseudoClassSettings.get(0)
                        ));
                    }
                } else if (pseudoClassSettings.size() == 2) {
                    try {
                        firstSettingNr = Integer.parseInt(pseudoClassSettings.get(0));
                    } catch (NumberFormatException e) {
                        throw new DocumentBuildException(String.format(
                                "Could not parse pseudo class setting for the page style block pseudo class '%s'. Encountered value %s while we anticipated an integer.",
                                pseudoClassName,
                                pseudoClassSettings.get(0)
                        ));
                    }

                    try {
                        secondSettingNr = Integer.parseInt(pseudoClassSettings.get(1));
                    } catch (NumberFormatException e) {
                        throw new DocumentBuildException(String.format(
                                "Could not parse pseudo class setting for the page style block pseudo class '%s'. Encountered value %s while we anticipated an integer.",
                                pseudoClassName,
                                pseudoClassSettings.get(1)
                        ));
                    }
                }

                if (pseudoClassName.equals("first-page")) {
                    range = new PageRange(1, 1);
                    if (firstSettingNr != -1) {
                        range = new PageRange(firstSettingNr + 1, firstSettingNr + 1);
                    }
                } else if (pseudoClassName.equals("page")) {
                    if (firstSettingNr == -1 && secondSettingNr == -1) {
                        throw new DocumentBuildException("You need to specify the page you want to act upon with the :page() pseudo class. For example :page(2) to select the second page");
                    } else if (firstSettingNr != -1 && secondSettingNr == -1) {
                        // Select one page
                        range = new PageRange(firstSettingNr, firstSettingNr);
                    } else {
                        // Select a whole page range
                        range = new PageRange(firstSettingNr, secondSettingNr);
                    }
                }
            }

            StyleValue headerStyle = block.getStyles().get(StyleType.HEADER);
            if (headerStyle != null) {
                HeaderFooterEntry entry = new HeaderFooterEntry();
                entry.isHeader = true;
                entry.folder = headerStyle.value();
                entry.range = range;
                entries.add(entry);
            }

            StyleValue footerStyle = block.getStyles().get(StyleType.FOOTER);
            if (footerStyle != null) {
                HeaderFooterEntry entry = new HeaderFooterEntry();
                entry.isHeader = false;
                entry.folder = footerStyle.value();
                entry.range = range;
                entries.add(entry);
            }
        }

        for (HeaderFooterEntry entry : entries) {
            File file = new File(ThawContext.getInstance().getCurrentFolder(), entry.folder);
            DocumentNode headerFooterNode = ctx.loadHeaderFooterNode(file);

            if (entry.isHeader) {
                ctx.getHeaderNodes().put(entry.range, headerFooterNode);
            } else {
                ctx.getFooterNodes().put(entry.range, headerFooterNode);
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
                ctx
        );
    }

    /**
     * Convert to a root node.
     *
     * @param node to convert
     * @param ctx  the build context
     * @return the root document node
     */
    private DocumentNode processRootNode(RootNode node, DocumentBuildContext ctx) throws DocumentBuildException {
        DocumentNode documentNode = new DocumentNode(node, null, ctx.getStyleModel().select(new StyleSelectorBuilder().build()));

        if (node.hasChildren()) {
            for (Node child : node.children()) {
                ctx.processBoxNode((BoxNode) child, documentNode);
            }
        }

        return documentNode;
    }

}
