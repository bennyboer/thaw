package de.be.thaw.typeset.knuthplass.converter.thingyhandler.impl;

import de.be.thaw.code.syntax.exception.HighlightException;
import de.be.thaw.code.syntax.impl.RTFSyntaxHighlighter;
import de.be.thaw.core.document.convert.exception.DocumentConversionException;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.text.model.tree.impl.ThingyNode;
import de.be.thaw.typeset.knuthplass.converter.context.ConversionContext;
import de.be.thaw.typeset.knuthplass.converter.thingyhandler.ThingyHandler;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.TextParagraph;
import de.be.thaw.typeset.knuthplass.paragraph.impl.code.CodeParagraph;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Set;

/**
 * Handler dealing with code block thingies.
 */
public class CodeHandler implements ThingyHandler {

    @Override
    public Set<String> getThingyNames() {
        return Set.of("CODE");
    }

    @Override
    public void handle(ThingyNode node, DocumentNode documentNode, ConversionContext ctx) throws DocumentConversionException {
        Paragraph currentParagraph = ctx.getCurrentParagraph();
        if (!(currentParagraph instanceof TextParagraph)) {
            throw new DocumentConversionException(String.format(
                    "Expected #CODE# Thingy at %s to be in a text paragraph",
                    node.getTextPosition()
            ));
        }

        TextParagraph paragraph = (TextParagraph) currentParagraph;
        if (!paragraph.isEmpty()) {
            throw new DocumentConversionException(String.format(
                    "Expected #CODE# Thingy at %s to be it's own paragraph and not being in-line with other text.",
                    node.getTextPosition()
            ));
        }

        // Try to read code from Thingy arguments and options
        String code = readSourceCode(node, ctx);

        int startLine = 1;
        int endLine = Integer.MAX_VALUE;
        String startLineStr = node.getOptions().get("startline");
        if (startLineStr != null) {
            try {
                startLine = Integer.parseInt(startLineStr);
            } catch (NumberFormatException e) {
                throw new DocumentConversionException(String.format(
                        "Expected value of option 'startLine' of #CODE# Thingy at %s to be an integer",
                        node.getTextPosition()
                ), e);
            }
        }
        String endLineStr = node.getOptions().get("endline");
        if (endLineStr != null) {
            try {
                endLine = Integer.parseInt(endLineStr);
            } catch (NumberFormatException e) {
                throw new DocumentConversionException(String.format(
                        "Expected value of option 'endLine' of #CODE# Thingy at %s to be an integer",
                        node.getTextPosition()
                ), e);
            }
        }

        // Syntax highlight code (build RTF from code).
        String rtfCode = syntaxHighlight(code, node, ctx);

        // Read additional options
        String caption = node.getOptions().get("caption");
        String captionPrefix = node.getOptions().get("caption-prefix");

        // Finalize the current paragraph -> we'll create a special code block paragraph instead
        ctx.finalizeParagraph();
        ctx.setCurrentParagraph(new CodeParagraph(
                rtfCode,
                startLine,
                endLine,
                ctx.getLineWidth(),
                documentNode,
                caption,
                captionPrefix
        ));
    }

    /**
     * Read the source code for the given thingy node.
     *
     * @param node to read source code for
     * @param ctx  the context
     * @return the read source code
     * @throws DocumentConversionException in case the source code could not be read/determined
     */
    private String readSourceCode(ThingyNode node, ConversionContext ctx) throws DocumentConversionException {
        String sourceFile = node.getOptions().get("src");
        if (sourceFile != null) {
            File currentProcessingFolder = ctx.getConfig().getWorkingDirectory();
            File sourceCodeFile = new File(currentProcessingFolder, sourceFile);

            StringBuilder sb = new StringBuilder();
            try (BufferedReader br = new BufferedReader(new InputStreamReader(new FileInputStream(sourceCodeFile)))) {
                String line = br.readLine();
                while (line != null) {
                    sb.append(line);

                    line = br.readLine();
                    if (line != null) {
                        sb.append('\n');
                    }
                }
            } catch (IOException e) {
                throw new DocumentConversionException(String.format(
                        "Could not read source code file at '%s' to be syntax highlighted in a code block",
                        sourceCodeFile.getAbsolutePath()
                ), e);
            }

            return sb.toString();
        } else if (!node.getArguments().isEmpty()) {
            return node.getArguments().iterator().next();
        } else {
            throw new DocumentConversionException(String.format(
                    "#CODE# thingy at %s requires either the code to show as the first argument or the 'src' option that points to the source code file to read in.",
                    node.getTextPosition()
            ));
        }
    }

    /**
     * Do syntax highlighting on the passed code.
     *
     * @param code to do syntax highlighting for
     * @param node the code thingy node
     * @param ctx  the conversion context
     * @return the syntax highlighted code
     * @throws DocumentConversionException in case the code could not be syntax highlighted
     */
    private String syntaxHighlight(String code, ThingyNode node, ConversionContext ctx) throws DocumentConversionException {
        String sourceFile = node.getOptions().get("src");
        boolean isAlreadyRTFCode = sourceFile != null && sourceFile.endsWith(".rtf");
        if (isAlreadyRTFCode) {
            return code;
        } else {
            String language = node.getOptions().get("language");
            if (language == null) {
                throw new DocumentConversionException(String.format(
                        "#CODE# thingy at %s requires the 'language' option to be set in order to properly syntax highlight the given source code file in the 'src' option.",
                        node.getTextPosition()
                ));
            }
            String style = node.getOptions().getOrDefault("style", "colorful");

            RTFSyntaxHighlighter syntaxHighlighter = new RTFSyntaxHighlighter();
            syntaxHighlighter.setWorkingDirectory(ctx.getConfig().getWorkingDirectory());

            try {
                return syntaxHighlighter.highlight(code, language, style.toLowerCase());
            } catch (HighlightException e) {
                throw new DocumentConversionException(e);
            }
        }
    }

}
