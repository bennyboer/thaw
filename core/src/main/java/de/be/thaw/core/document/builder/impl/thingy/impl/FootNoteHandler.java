package de.be.thaw.core.document.builder.impl.thingy.impl;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.shared.ThawContext;
import de.be.thaw.style.model.selector.builder.StyleSelectorBuilder;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
import de.be.thaw.style.model.style.value.DoubleStyleValue;
import de.be.thaw.style.model.style.value.StyleValue;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.util.unit.Unit;

import java.io.StringReader;
import java.util.Set;

/**
 * Handler dealing with foot notes.
 */
public class FootNoteHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("FOOTNOTE");
    }

    @Override
    public void handle(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException {
        if (thingyNode.getArguments().isEmpty()) {
            // Expected a string describing the foot note
            throw new DocumentBuildException(String.format(
                    "#FOOTNOTE# Thingy at %s: Expected to have an argument describing the foot note",
                    thingyNode.getTextPosition()
            ));
        }

        String footNoteText = thingyNode.getArguments().iterator().next();
        TextModel textModel;
        try (StringReader sr = new StringReader(footNoteText)) {
            textModel = ThawContext.getInstance().getTextParser().parse(sr);
        } catch (ParseException e) {
            throw new DocumentBuildException(String.format(
                    "Could not parse foot note text of #FOOTNOTE# Thingy at %s",
                    thingyNode.getTextPosition()
            ), e);
        }

        // Get root node
        DocumentNode root = documentNode;
        while (root.getParent() != null) {
            root = root.getParent();
        }

        // Create new document root node for the foot note
        DocumentNode footNoteRoot = new DocumentNode(
                textModel.getRoot(),
                null,
                ctx.getStyleModel().select(new StyleSelectorBuilder().build())
        );

        // Create nodes from the foot note description text
        for (Node node : textModel.getRoot().children()) {
            if (node.getType() == NodeType.BOX) {
                ctx.processBoxNode((BoxNode) node, footNoteRoot);
            }
        }

        // Create dummy node to represent the foot note number
        DocumentNode firstBoxNode = footNoteRoot.getChildren().get(0);

        StyleValue fontSizeValue = documentNode.getStyles()
                .resolve(StyleType.FONT_SIZE)
                .orElseThrow();
        double fontSize = Unit.convert(fontSizeValue.doubleValue(), fontSizeValue.unit().orElse(Unit.POINTS), Unit.POINTS);

        Styles footNoteNumberStyles = new Styles(firstBoxNode.getStyles());
        footNoteNumberStyles.overrideStyle(StyleType.FONT_SIZE, new DoubleStyleValue(Math.max(8.0, fontSize * 0.6), Unit.POINTS));

        DocumentNode fakeNumberingNode = new DocumentNode(
                new TextNode(String.format("%d ", ctx.getFootNotes().size() + 1), null),
                null,
                footNoteNumberStyles
        );
        firstBoxNode.getChildren().add(0, fakeNumberingNode); // Add new foot note number node

        // Add foot note mapping for the current document node
        ctx.getFootNotes().put(documentNode.getId(), footNoteRoot);
    }

}

