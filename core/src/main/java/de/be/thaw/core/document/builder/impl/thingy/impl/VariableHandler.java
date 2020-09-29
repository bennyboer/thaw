package de.be.thaw.core.document.builder.impl.thingy.impl;

import de.be.thaw.core.document.builder.impl.DocumentBuildContext;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.exception.MissingReferenceTargetException;
import de.be.thaw.core.document.builder.impl.thingy.ThingyHandler;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.TextNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;

import java.util.Set;

/**
 * Handler dealing with variable thingies.
 */
public class VariableHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("VAR");
    }

    @Override
    public void handle(ThingyNode thingyNode, DocumentNode documentNode, DocumentBuildContext ctx) throws DocumentBuildException {
        if (thingyNode.getArguments().isEmpty()) {
            throw new MissingReferenceTargetException(String.format(
                    "Expected #VAR# Thingy to include a variable name defined in the Thaw document info file (*.tdi) as first argument at %s",
                    thingyNode.getTextPosition()
            ));
        }

        String variableKey = thingyNode.getArguments().iterator().next()
                .trim()
                .toLowerCase();

        String value = ctx.getInfo()
                .getVariable(variableKey)
                .orElseThrow(() -> new DocumentBuildException(String.format(
                        "#VAR# Thingy at %s specifies key '%s' that is not included in the Thaw document info file (*.tdi)",
                        thingyNode.getTextPosition(),
                        variableKey
                )));

        // Add document node representing the variable value text
        new DocumentNode(
                new TextNode(value, thingyNode.getTextPosition()),
                documentNode.getParent(),
                documentNode.getStyles()
        );
    }

}

