package de.be.thaw.typeset.knuthplass.paragraph.impl.code;

import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.typeset.knuthplass.paragraph.AbstractParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.ParagraphType;

/**
 * Paragraph representing a code block.
 */
public class CodeParagraph extends AbstractParagraph {

    /**
     * The code formatted in RTF-format.
     */
    private final String rtfCode;

    public CodeParagraph(String rtfCode, double lineWidth, DocumentNode node) {
        super(lineWidth, node);

        this.rtfCode = rtfCode;
    }

    @Override
    public ParagraphType getType() {
        return ParagraphType.CODE;
    }

    /**
     * Get the RTF-formatted code.
     *
     * @return RTF-formatted code
     */
    public String getRtfCode() {
        return rtfCode;
    }

}
