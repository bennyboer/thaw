package de.be.thaw.export.pdf;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.export.Exporter;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.element.ElementExporters;
import de.be.thaw.export.pdf.util.PdfImageSource;
import de.be.thaw.hyphenation.HyphenationDictionaries;
import de.be.thaw.hyphenation.HyphenationDictionary;
import de.be.thaw.info.model.language.Language;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.SizeStyle;
import de.be.thaw.typeset.TypeSetter;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.KnuthPlassTypeSetter;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.config.util.FontDetailsSupplier;
import de.be.thaw.typeset.knuthplass.config.util.GlueConfig;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWord;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.HyphenatedWordPart;
import de.be.thaw.typeset.knuthplass.config.util.hyphen.Hyphenator;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.typeset.util.Size;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/**
 * Exporter exporting documents to PDF.
 */
public class PdfExporter implements Exporter {

    /**
     * Logger for the class.
     */
    private static final Logger LOGGER = Logger.getLogger(PdfExporter.class.getSimpleName());

    /**
     * Printer points per inch.
     */
    private static final float POINTS_PER_INCH = 72;

    /**
     * Printer points per millimeter.
     */
    private static final double POINTS_PER_MM = 1 / (10 * 2.54f) * POINTS_PER_INCH;

    /**
     * Maximum iterations when trying to typeset properly.
     * This is needed because the used Knuth-Plass line-breaking algorithm
     * might not find a feasible solution.
     */
    private static final int MAX_TYPESETTING_ITERATIONS = 10;

    @Override
    public void export(Document document, Path path) throws ExportException {
        try (PDDocument doc = new PDDocument()) {
            ExportContext ctx = new ExportContext(doc);

            SizeStyle sizeStyle = document.getRoot().getStyle().getStyleAttribute(
                    StyleType.SIZE,
                    style -> Optional.ofNullable(((SizeStyle) style))
            ).orElseThrow();
            ctx.setPageSize(new Size(
                    sizeStyle.getWidth() * POINTS_PER_MM,
                    sizeStyle.getHeight() * POINTS_PER_MM
            ));

            InsetsStyle insetsStyle = document.getRoot().getStyle().getStyleAttribute(
                    StyleType.INSETS,
                    style -> Optional.ofNullable(((InsetsStyle) style))
            ).orElseThrow();
            ctx.setPageInsets(new Insets(
                    insetsStyle.getTop() * POINTS_PER_MM,
                    insetsStyle.getLeft() * POINTS_PER_MM,
                    insetsStyle.getBottom() * POINTS_PER_MM,
                    insetsStyle.getRight() * POINTS_PER_MM
            ));

            List<Page> pages;
            try {
                pages = typeset(document, ctx);
            } catch (TypeSettingException e) {
                throw new ExportException(e);
            }

            exportToPages(pages, ctx);

            doc.save(path.toFile());
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    /**
     * Export the passed pages using the given export context to PDF pages.
     *
     * @param pages to export to PDF pages
     * @param ctx   the export context
     * @throws ExportException in case the page export did not work
     */
    private void exportToPages(List<Page> pages, ExportContext ctx) throws ExportException {
        PDRectangle pageRect = new PDRectangle(
                (float) ctx.getPageSize().getWidth(),
                (float) ctx.getPageSize().getHeight()
        );

        try {
            // Create initial page
            PDPage pdfPage = new PDPage(pageRect);
            ctx.getDocument().addPage(pdfPage);
            ctx.setCurrentPage(pdfPage);
            ctx.setContentStream(new PDPageContentStream(ctx.getDocument(), ctx.getCurrentPage()));

            int pageCounter = 0;
            for (Page page : pages) {
                ctx.setCurrentSourcePage(page);

                int len = page.getElements().size();
                for (int i = 0; i < len; i++) {
                    ctx.setCurrentSourceElementIndex(i);
                    Element element = page.getElements().get(i);

                    ElementExporter elementExporter = ElementExporters.getForType(element.getType()).orElseThrow(() -> new ExportException(String.format(
                            "Elements of type '%s' cannot be exported as there is no suitable exporter",
                            element.getType().name()
                    )));

                    elementExporter.export(element, ctx);
                }

                ctx.getContentStream().close();

                if (pageCounter < pages.size() - 1) {
                    // Create next PDF page
                    pdfPage = new PDPage(pageRect);
                    ctx.getDocument().addPage(pdfPage);
                    ctx.setCurrentPage(pdfPage);
                    ctx.setContentStream(new PDPageContentStream(ctx.getDocument(), ctx.getCurrentPage()));
                }

                pageCounter++;
            }
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    /**
     * Try to type set the passed document.
     *
     * @param document to type set
     * @param ctx      the export context to use
     * @return the typeset pages
     * @throws TypeSettingException in case the document could not be type set
     */
    private List<Page> typeset(Document document, ExportContext ctx) throws TypeSettingException {
        TypeSettingException lastException = null;
        for (int quality = 0; quality < MAX_TYPESETTING_ITERATIONS; quality++) {
            TypeSetter typeSetter = createTypeSetter(ctx, document.getInfo().getLanguage(), quality);

            try {
                return typeSetter.typeset(document);
            } catch (TypeSettingException e) {
                lastException = e;

                LOGGER.log(Level.FINER, String.format("%s > Will decrease type setting quality in order to succeed eventually", e.getMessage()));
            }
        }

        throw new TypeSettingException("Could not typeset the pages properly despite decreasing the quality multiply times", lastException);
    }

    /**
     * Create a type setter.
     *
     * @param ctx      the export context
     * @param language to use
     * @param quality  the quality (0 is the best, higher get worse)
     * @return type setter to use
     */
    private TypeSetter createTypeSetter(ExportContext ctx, Language language, int quality) throws TypeSettingException {
        HyphenationDictionary hyphenationDictionary = HyphenationDictionaries.getDictionary(language).orElseThrow(() -> new TypeSettingException(String.format(
                "Could not find the hyphenation dictionary for language '%s'",
                language.name()
        )));

        return new KnuthPlassTypeSetter(KnuthPlassTypeSettingConfig.newBuilder()
                .setPageSize(ctx.getPageSize())
                .setPageInsets(ctx.getPageInsets())
                .setLooseness(1 + quality)
                .setFontDetailsSupplier(new FontDetailsSupplier() {
                    @Override
                    public double getStringWidth(DocumentNode node, String str) throws Exception {
                        return ctx.getFontForNode(node).getStringWidth(str) / 1000 * ctx.getFontSizeForNode(node);
                    }

                    @Override
                    public double getSpaceWidth(DocumentNode node) throws Exception {
                        return ctx.getFontForNode(node).getSpaceWidth() / 1000 * ctx.getFontSizeForNode(node) / 2;
                    }
                })
                .setGlueConfig(new GlueConfig() {
                    @Override
                    public double getInterWordStretchability(DocumentNode node, char lastChar) throws Exception {
                        return (ctx.getFontForNode(node).getSpaceWidth() / 1000 * ctx.getFontSizeForNode(node) / 2) * (quality + 1);
                    }

                    @Override
                    public double getInterWordShrinkability(DocumentNode node, char lastChar) throws Exception {
                        return ctx.getFontForNode(node).getSpaceWidth() / 1000 * ctx.getFontSizeForNode(node) / 3;
                    }
                })
                .setHyphenator(new Hyphenator() {
                    @Override
                    public HyphenatedWord hyphenate(String word) {
                        return new HyphenatedWord(hyphenationDictionary.hyphenate(word)
                                .stream()
                                .map(HyphenatedWordPart::new)
                                .collect(Collectors.toList()));
                    }

                    @Override
                    public double getExplicitHyphenPenalty() {
                        return HyphenatedWordPart.DEFAULT_PENALTY;
                    }
                })
                .setImageSourceSupplier(src -> new PdfImageSource(PDImageXObject.createFromFile(src, ctx.getDocument())))
                .build());
    }

}
