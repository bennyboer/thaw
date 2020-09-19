package de.be.thaw.core.document.builder.impl.thingy.impl;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.shared.ThawContext;
import de.be.thaw.style.model.impl.DefaultStyleModel;
import de.be.thaw.style.parser.exception.StyleModelParseException;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.model.tree.impl.BoxNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.text.parser.exception.ParseException;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Handler dealing with the include thingy.
 */
public class IncludeHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("INCLUDE");
    }

    @Override
    public void handle(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException {
        if (thingyNode.getArguments().isEmpty()) {
            throw new DocumentBuildException(String.format(
                    "Expected #INCLUDE# Thingy at %s to have one argument describing the project subfolder to include",
                    thingyNode.getTextPosition()
            ));
        }

        // Check if the thingy node is the first child of a box node
        if (thingyNode.getParent() == null || thingyNode.getParent().getType() != NodeType.BOX) {
            throw new DocumentBuildException(String.format(
                    "Expected #INCLUDE# Thingy at %s to be the only thing in a paragraph",
                    thingyNode.getTextPosition()
            ));
        }
        BoxNode parent = (BoxNode) thingyNode.getParent();
        if (parent.children().get(0) != thingyNode) {
            throw new DocumentBuildException(String.format(
                    "Expected #INCLUDE# Thingy at %s to be the only thing in a paragraph",
                    thingyNode.getTextPosition()
            ));
        }

        File currentProcessingFolder = ThawContext.getInstance().getCurrentFolder();

        String subFolderName = thingyNode.getArguments().iterator().next();
        File subFolder = new File(currentProcessingFolder, subFolderName);

        // Check if sub folder exists
        if (!subFolder.exists()) {
            throw new DocumentBuildException(String.format(
                    "Project subfolder '%s' to include using the #INCLUDE# Thingy at %s does not exist",
                    subFolder.getAbsolutePath(),
                    thingyNode.getTextPosition()
            ));
        }

        // Find and parse text file
        String[] textFiles = subFolder.list((dir, name) -> name.endsWith(".tdt"));
        if (textFiles.length == 0) {
            throw new DocumentBuildException(String.format(
                    "Could not find a text file (ending with *.tdt) in the folder specified in the #INCLUDE# Thingy at %s",
                    thingyNode.getTextPosition()
            ));
        } else if (textFiles.length > 1) {
            throw new DocumentBuildException(String.format(
                    "Could find more than one text file (ending with *.tdt) in the folder specified in the #INCLUDE# Thingy at %s",
                    thingyNode.getTextPosition()
            ));
        }
        File textFile = new File(subFolder, textFiles[0]);

        TextModel textModel;
        try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(textFile), ThawContext.getInstance().getEncoding()))) {
            textModel = ThawContext.getInstance().getTextParser().parse(br);
        } catch (IOException | ParseException e) {
            throw new DocumentBuildException(String.format(
                    "Could not parse text file at '%s' included by the #INCLUDE# Thingy at %s",
                    textFile.getAbsolutePath(),
                    thingyNode.getTextPosition()
            ), e);
        }

        // Find and parse style file (if there is one, otherwise take the current one).
        DefaultStyleModel styleModel = ctx.getStyleModel();
        String[] styleFiles = subFolder.list((dir, name) -> name.endsWith(".tds"));
        if (styleFiles.length > 1) {
            throw new DocumentBuildException(String.format(
                    "Could find more than one style file (ending with *.tds) in the folder specified in the #INCLUDE# Thingy at %s",
                    thingyNode.getTextPosition()
            ));
        } else if (styleFiles.length == 1) {
            File styleFile = new File(subFolder, styleFiles[0]);

            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(styleFile), ThawContext.getInstance().getEncoding()))) {
                styleModel = ThawContext.getInstance().getStyleParser().parse(br);
            } catch (IOException | StyleModelParseException e) {
                throw new DocumentBuildException(String.format(
                        "Could not parse style file at '%s' included by the #INCLUDE# Thingy at %s",
                        styleFile.getAbsolutePath(),
                        thingyNode.getTextPosition()
                ));
            }

            // Merge current style model and the new one
            styleModel = styleModel.merge(ctx.getStyleModel());
        }

        // Process the nodes in the included files
        DocumentNode root = documentNode;
        while (root.getParent() != null) {
            root = root.getParent();
        }

        // SET CONTEXT TO THE NEW SETTINGS
        ThawContext.getInstance().setCurrentFolder(subFolder); // Set the currently processing folder for nested #INCLUDE# Thingies
        DefaultStyleModel oldStyleModel = ctx.getStyleModel();
        ctx.setStyleModel(styleModel); // Set the new style model

        // Create nodes in the included folder
        for (Node node : textModel.getRoot().children()) {
            if (node.getType() == NodeType.BOX) {
                ctx.processBoxNode((BoxNode) node, root, root.getStyle());
            }
        }

        // RESET TO OLD SETTINGS
        ThawContext.getInstance().setCurrentFolder(currentProcessingFolder); // Reset the currently processing folder
        ctx.setStyleModel(oldStyleModel); // Reset to the old style model
    }

}
