package de.be.thaw.reference.citation;

import de.be.thaw.reference.citation.source.Source;
import org.jetbrains.annotations.Nullable;

/**
 * A citation representation.
 */
public class Citation {

    /**
     * The source to cite.
     */
    private final Source source;

    /**
     * Position in the source (page, paragraph, ...).
     */
    @Nullable
    private final String position;

    /**
     * Whether we are dealing with a direct or indirect citation.
     */
    private final boolean direct;

    public Citation(Source source) {
        this(source, false, null);
    }

    public Citation(Source source, boolean direct) {
        this(source, direct, null);
    }

    public Citation(Source source, boolean direct, String position) {
        this.source = source;
        this.position = position;
        this.direct = direct;
    }

    public Source getSource() {
        return source;
    }

    public @Nullable String getPosition() {
        return position;
    }

    public boolean isDirect() {
        return direct;
    }

}
