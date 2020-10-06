package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Set;

/**
 * Handler dealing with table thingies.
 */
public class TableHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("TABLE");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        Paragraph currentParagraph = ctx.getCurrentParagraph();
        if (!(currentParagraph instanceof TextParagraph)) {
            throw new DocumentConversionException(String.format(
                    "Expected #TABLE# Thingy at %s to be in a text paragraph",
                    node.getTextPosition()
            ));
        }

        TextParagraph paragraph = (TextParagraph) currentParagraph;
        if (!paragraph.isEmpty()) {
            throw new DocumentConversionException(String.format(
                    "Expected #TABLE# Thingy at %s to be it's own paragraph and not being in-line with other text.",
                    node.getTextPosition()
            ));
        }

        String tableContent = readTableContent(node, ctx);

        System.out.println(tableContent);
    }

    /**
     * Read the table content as string.
     *
     * @param node to read content from
     * @param ctx  the conversion context
     * @return the read table content
     */
    private String readTableContent(ThingyNode node, ConversionContext ctx) throws DocumentConversionException {
        String tableSrc = node.getOptions().get("src");
        if (tableSrc == null) {
            // We expect then to have the content in the first argument
            tableSrc = node.getArguments().iterator().next();
            if (tableSrc == null) {
                throw new DocumentConversionException(String.format(
                        "#TABLE# Thingy at %s is expected to either specify a source file using the 'src' option or the contents of the table in the first argument",
                        node.getTextPosition()
                ));
            }
        } else {
            // Load table source from file
            File file = new File(ctx.getConfig().getWorkingDirectory(), tableSrc);
            try {
                tableSrc = Files.readString(file.toPath(), ctx.getDocument().getInfo().getEncoding());
            } catch (IOException e) {
                throw new DocumentConversionException(String.format(
                        "Could not read specified table source file at '%s' from #TABLE# Thingy at %s",
                        file.getAbsolutePath(),
                        node.getTextPosition()
                ));
            }
        }

        return tableSrc;
    }

}
