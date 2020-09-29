package de.be.thaw.text.model.tree.impl;

import de.be.thaw.text.model.emphasis.TextEmphasis;
import de.be.thaw.text.model.tree.Node;
import de.be.thaw.text.model.tree.NodeType;
import de.be.thaw.text.util.TextPosition;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A node that represents formatted text.
 */
public class FormattedNode extends Node {

    /**
     * The value the node is holding.
     */
    private final String value;

    /**
     * Position of the value in the original text.
     */
    private final TextPosition position;

    /**
     * What kind of emphases this nodes text is formatted with.
     */
    private final Set<TextEmphasis> emphases;

    /**
     * Custom class name that applies on the node.
     */
    @Nullable
    private final String className;

    public FormattedNode(String value, TextPosition position, Set<TextEmphasis> emphases, @Nullable String className) {
        super(NodeType.FORMATTED);

        this.value = value;
        this.position = position;
        this.emphases = emphases;
        this.className = className;
    }

    @Override
    public TextPosition getTextPosition() {
        if (hasChildren()) {
            return new TextPosition(
                    position.getStartLine(),
                    position.getStartPos(),
                    children().get(children().size() - 1).getTextPosition().getEndLine(),
                    children().get(children().size() - 1).getTextPosition().getEndPos()
            );
        } else {
            return position;
        }
    }

    @Override
    public String getInternalNodeRepresentation() {
        return String.format(
                "'%s' [%s]%s",
                getValue(),
                getEmphases().stream().map(TextEmphasis::name).sorted().collect(Collectors.joining(", ")),
                getClassName().isPresent() ? String.format(" (.%s)", getClassName().orElseThrow()) : ""
        );
    }

    /**
     * Get the nodes value.
     *
     * @return value
     */
    public String getValue() {
        return value;
    }

    /**
     * Get the emphases this node is formatted with.
     *
     * @return emphases
     */
    public Set<TextEmphasis> getEmphases() {
        return emphases;
    }

    /**
     * Get a custom class name that applies on the node.
     *
     * @return class name
     */
    public Optional<String> getClassName() {
        return Optional.ofNullable(className);
    }

}
