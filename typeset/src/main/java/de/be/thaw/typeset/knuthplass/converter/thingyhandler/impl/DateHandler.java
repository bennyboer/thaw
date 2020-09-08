package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.Set;

/**
 * Handler for dealing with date thingies.
 */
public class DateHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("DATE");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        if (!(ctx.getCurrentParagraph() instanceof TextParagraph)) {
            throw new DocumentConversionException(String.format(
                    "Expected the #DATE# Thingy to be inside a text paragraph at %s",
                    node.getTextPosition()
            ));
        }

        // Fetch the set locale.
        Locale locale = ctx.getDocument().getInfo().getLanguage().getLocale();

        // Parse the date format to use to display the current date
        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.DEFAULT, locale);
        String dateFormatStr = node.getOptions().get("format");
        if (dateFormatStr != null) {
            try {
                dateFormat = new SimpleDateFormat(dateFormatStr, locale);
            } catch (IllegalArgumentException e) {
                throw new DocumentConversionException(String.format(
                        "Provided illegal date format string '%s' in the 'format' option of the #DATE# Thingy at %s",
                        dateFormatStr,
                        node.getTextPosition()
                ));
            }
        }

        // Create the current date/time string for the current date time using the provided date format.
        Calendar calendar = Calendar.getInstance(locale);
        String dateStr = dateFormat.format(calendar.getTime());

        // Fetch the current paragraph and add the date string to it
        TextParagraph paragraph = (TextParagraph) ctx.getCurrentParagraph();
        ctx.appendTextToParagraph(paragraph, dateStr, documentNode.getParent());
    }

}
