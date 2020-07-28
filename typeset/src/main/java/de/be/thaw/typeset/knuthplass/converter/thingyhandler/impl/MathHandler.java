package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.math.mathml.parser.MathMLParser;
import de.be.thaw.math.mathml.parser.MathMLParserConfig;
import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.DefaultMathMLParser;
import de.be.thaw.math.mathml.tree.MathMLTree;
import de.be.thaw.math.mathml.typeset.MathExpression;
import de.be.thaw.math.mathml.typeset.MathMLTypesetter;
import de.be.thaw.math.mathml.typeset.config.MathTypesetConfig;
import de.be.thaw.math.mathml.typeset.exception.TypesetException;
import de.be.thaw.math.mathml.typeset.impl.DefaultMathMLTypesetter;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.FontStyle;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.item.impl.box.MathBox;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.math.MathParagraph;
import de.be.thaw.util.HorizontalAlignment;

import java.io.ByteArrayInputStream;
import java.util.Optional;
import java.util.Set;

/**
 * Handler for math thingies.
 */
public class MathHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("MATH");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        Paragraph currentParagraph = ctx.getCurrentParagraph();
        if (!(currentParagraph instanceof TextParagraph)) {
            throw new DocumentConversionException(String.format(
                    "Expected #MATH# Thingy at %s to be in a text paragraph",
                    node.getTextPosition()
            ));
        }
        TextParagraph paragraph = (TextParagraph) currentParagraph;

        if (node.getArguments().isEmpty()) {
            throw new DocumentConversionException(String.format(
                    "Expected #MATH# Thingy at %s to have a math expression as argument",
                    node.getTextPosition()
            ));
        }

        String expression = node.getArguments().iterator().next();

        // TODO Check if expression is MathML or TeX -> if TeX convert to MathML

        // Prepare some constants
        final double fontSize = documentNode.getStyle().getStyleAttribute(
                StyleType.FONT,
                style -> Optional.ofNullable(((FontStyle) style).getSize())
        ).orElse(11.0);

        // Parse MathML
        MathMLParser parser = new DefaultMathMLParser();
        MathMLTree tree;
        try {
            tree = parser.parse(new ByteArrayInputStream(expression.getBytes()), new MathMLParserConfig(
                    fontSize * 0.05
            ));
        } catch (ParseException e) {
            throw new DocumentConversionException(String.format(
                    "Could not parse math expression from MathML at #MATH# Thingy at %s. Error message was: %s",
                    node.getTextPosition(),
                    e.getMessage()
            ), e);
        }

        // Typeset MathML tree
        MathMLTypesetter typesetter = new DefaultMathMLTypesetter();
        MathExpression ex;
        try {
            ex = typesetter.typeset(tree, new MathTypesetConfig(ctx.getConfig().getMathFont(), fontSize));
        } catch (TypesetException e) {
            throw new DocumentConversionException(String.format(
                    "Could not typeset math expression from #MATH# Thingy at %s. Error message was: %s",
                    node.getTextPosition(),
                    e.getMessage()
            ), e);
        }

        if (paragraph.isEmpty()) {
            // This math expression is a math paragraph and not in-line with text!

            // Finalize the current paragraph
            ctx.finalizeParagraph();

            // Fetch the alignment
            HorizontalAlignment alignment = HorizontalAlignment.CENTER;
            String alignmentStr = node.getOptions().get("alignment");
            if (alignmentStr != null) {
                alignment = HorizontalAlignment.valueOf(alignmentStr.toUpperCase());
            }

            ctx.setCurrentParagraph(new MathParagraph(
                    ctx.getLineWidth(),
                    documentNode,
                    ex,
                    alignment
            ));
        } else {
            paragraph.addItem(new MathBox(ex));
        }
    }

}
