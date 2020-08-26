package de.be.thaw.core.document.builder.impl.thingy.impl;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.reference.citation.styles.referencelist.ReferenceListEntry;
import de.be.thaw.shared.ThawContext;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.text.parser.exception.ParseException;

import java.io.StringReader;
import java.util.List;
import java.util.Set;

/**
 * Handler dealing with the references thingy.
 * It will append a reference list to the document.
 */
public class ReferenceListHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("REFERENCES");
    }

    @Override
    public void handle(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException {
        // Find root node
        DocumentNode root = documentNode;
        while (root.getParent() != null) {
            root = root.getParent();
        }

        // Fetch reference list entries
        List<ReferenceListEntry> entries = ctx.getSourceModel().getStyle().getReferenceListEntries();

        // Build document nodes for all reference list entries
        for (ReferenceListEntry entry : entries) {
            // Parse entry to text model
            TextModel textModel;
            try (StringReader sr = new StringReader(entry.getEntry())) {
                textModel = ThawContext.getInstance().getTextParser().parse(sr);
            } catch (ParseException e) {
                throw new DocumentBuildException(String.format(
                        "Could not parse reference list entry '%s' for reference list #REFERENCES# thingy at %s",
                        entry.getEntry(),
                        thingyNode.getTextPosition()
                ), e);
            }

            boolean isFirst = true;
            for (Node node : textModel.getRoot().children()) {
                if (node.getType() == NodeType.BOX) {

                    ctx.processBoxNode((BoxNode) node, root, root.getStyle());
                    if (isFirst) {
                        isFirst = false;

                        // Add identifier to the label to node ID mapping to find internal document references later
                        List<DocumentNode> firstChildren = root.getChildren().get(root.getChildren().size() - 1).getChildren();
                        DocumentNode firstChild = firstChildren.get(0);

                        ctx.getLabelToNodeID().put(entry.getIdentifier(), firstChild.getId());
                    }
                }
            }
        }
    }

}