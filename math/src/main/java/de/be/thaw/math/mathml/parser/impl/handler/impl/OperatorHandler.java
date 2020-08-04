package de.be.thaw.math.mathml.parser.impl.handler.impl;

import de.be.thaw.math.mathml.parser.exception.ParseException;
import de.be.thaw.math.mathml.parser.impl.context.MathMLParseContext;
import de.be.thaw.math.mathml.tree.node.MathMLNode;
import de.be.thaw.math.mathml.tree.node.MathVariant;
import de.be.thaw.math.mathml.tree.node.impl.OperatorNode;
import de.be.thaw.math.mathml.tree.util.operator.OperatorDictionary;
import de.be.thaw.math.mathml.tree.util.operator.OperatorDictionaryEntry;
import de.be.thaw.math.mathml.tree.util.operator.OperatorForm;
import org.jsoup.nodes.Element;

import java.util.Optional;

/**
 * Handler for <mo> MathML operator nodes.
 */
public class OperatorHandler extends TokenNodeHandler {

    /**
     * Default space width.
     * Default value for lspace and rspace attributes.
     */
    private static final double DEFAULT_SPACE_WIDTH = 0.0;

    public OperatorHandler() {
        super("mo");
    }

    @Override
    public MathMLNode parse(Element element, MathMLParseContext ctx) throws ParseException {
        String text = element.text();
        if (text.isBlank()) {
            throw new ParseException("Encountered an <mo> node without content");
        }

        // Determine form
        OperatorForm form = OperatorForm.INFIX;
        if (element.parent().nodeName().equals("mrow")) {
            if (element.lastElementSibling() == element) {
                form = OperatorForm.POSTFIX;
            } else if (element.firstElementSibling() == element) {
                form = OperatorForm.PREFIX;
            }
        }

        // Determine default attribute values
        double lspace = DEFAULT_SPACE_WIDTH;
        double rspace = DEFAULT_SPACE_WIDTH;

        Optional<OperatorDictionaryEntry> optEntry = OperatorDictionary.getEntry(form, text.trim().toCharArray());
        if (optEntry.isPresent()) {
            OperatorDictionaryEntry entry = optEntry.orElseThrow();

            lspace = entry.getLspace();
            rspace = entry.getRspace();
        }

        // Parse attributes
        MathVariant mathVariant = parseMathVariant(element, MathVariant.NORMAL);
        double mathSize = parseMathSize(element, 1.0);
        lspace = getDoubleAttribute(element, "lspace", lspace);
        rspace = getDoubleAttribute(element, "rspace", rspace);

        // TODO Add attributes that we want to support (form, ...)

        // TODO Determine the form attribute automatically based on the operator (opening parathesis, closing, semicolon, ...)

        return new OperatorNode(text, mathVariant, mathSize, lspace, rspace);
    }

}
