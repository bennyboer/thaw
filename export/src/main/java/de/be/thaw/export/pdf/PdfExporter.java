package de.be.thaw.export.pdf;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.export.Exporter;
import de.be.thaw.export.exception.ExportException;
import de.be.thaw.export.pdf.element.ElementExporter;
import de.be.thaw.export.pdf.element.ElementExporters;
import de.be.thaw.export.pdf.font.ThawPdfFont;
import de.be.thaw.export.pdf.util.ElementLocator;
import de.be.thaw.export.pdf.util.ExportContext;
import de.be.thaw.export.pdf.util.PdfImageSource;
import de.be.thaw.font.ThawFont;
import de.be.thaw.font.util.KernedSize;
import de.be.thaw.hyphenation.HyphenationDictionaries;
import de.be.thaw.hyphenation.HyphenationDictionary;
import de.be.thaw.info.model.language.Language;
import de.be.thaw.math.util.MathFont;
import de.be.thaw.shared.ThawContext;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.Styles;
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
import de.be.thaw.util.Size;
import de.be.thaw.util.unit.Unit;
import org.apache.fontbox.ttf.TTFParser;
import org.apache.fontbox.ttf.TrueTypeFont;
import org.apache.pdfbox.multipdf.LayerUtility;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.graphics.form.PDFormXObject;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
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

    @Override
    public void export(Document document, Path path) throws ExportException {
        try (PDDocument doc = new PDDocument()) {
            ExportContext ctx = new ExportContext(doc, document);

            Styles styles = document.getRoot().getStyles();

            final double width = styles.resolve(StyleType.WIDTH).orElseThrow().doubleValue(Unit.POINTS);
            final double height = styles.resolve(StyleType.HEIGHT).orElseThrow().doubleValue(Unit.POINTS);

            ctx.setPageSize(new Size(width, height));

            final double marginTop = styles.resolve(StyleType.MARGIN_TOP).orElseThrow().doubleValue(Unit.POINTS);
            final double marginBottom = styles.resolve(StyleType.MARGIN_BOTTOM).orElseThrow().doubleValue(Unit.POINTS);
            final double marginLeft = styles.resolve(StyleType.MARGIN_LEFT).orElseThrow().doubleValue(Unit.POINTS);
            final double marginRight = styles.resolve(StyleType.MARGIN_RIGHT).orElseThrow().doubleValue(Unit.POINTS);

            final double paddingTop = styles.resolve(StyleType.PADDING_TOP).orElseThrow().doubleValue(Unit.POINTS);
            final double paddingBottom = styles.resolve(StyleType.PADDING_BOTTOM).orElseThrow().doubleValue(Unit.POINTS);
            final double paddingLeft = styles.resolve(StyleType.PADDING_LEFT).orElseThrow().doubleValue(Unit.POINTS);
            final double paddingRight = styles.resolve(StyleType.PADDING_RIGHT).orElseThrow().doubleValue(Unit.POINTS);

            ctx.setPageInsets(new Insets(
                    marginTop + paddingTop,
                    marginLeft + paddingLeft,
                    marginBottom + paddingBottom,
                    marginRight + paddingRight
            ));

            ThawFont mathFont;
            try {
                TTFParser ttfParser = new TTFParser();
                TrueTypeFont ttf = ttfParser.parse(MathFont.getMathFontStream());

                mathFont = new ThawPdfFont(ttf, ctx.getPdDocument());
            } catch (IOException e) {
                throw new ExportException(e);
            }
            ctx.setMathFont(mathFont);

            // Typeset the document to individual pages
            List<Page> pages;
            try {
                pages = typeset(document, ctx);
            } catch (TypeSettingException e) {
                throw new ExportException(e);
            }

            // Build lookup from original DocumentNode ID to the typeset element
            ctx.setElementLookup(buildElementLookup(pages));

            exportToPages(pages, ctx);

            doc.save(path.toFile());
        } catch (IOException e) {
            throw new ExportException(e);
        }
    }

    /**
     * Build a lookup from the original DocumentNode ID to the typeset element.
     *
     * @param pages to get elements for
     * @return lookup
     */
    private Map<String, ElementLocator> buildElementLookup(List<Page> pages) {
        Map<String, ElementLocator> elementLookup = new HashMap<>();

        for (Page page : pages) {
            for (Element element : page.getElements()) {
                element.getNode().ifPresent(node -> elementLookup.put(node.getId(), new ElementLocator(page.getNumber(), element)));
            }
        }

        return elementLookup;
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
            ctx.getPdDocument().addPage(pdfPage);
            ctx.setCurrentPage(pdfPage);
            ctx.setContentStream(new PDPageContentStream(ctx.getPdDocument(), ctx.getCurrentPage()));

            // Export all elements from all pages to PDF
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
                    ctx.getPdDocument().addPage(pdfPage);
                    ctx.setCurrentPage(pdfPage);
                    ctx.setContentStream(new PDPageContentStream(ctx.getPdDocument(), ctx.getCurrentPage()));
                }

                pageCounter++;
            }

            // Run after export hooks for each element again (for example to create links)
            for (Page page : pages) {
                for (Element element : page.getElements()) {
                    ElementExporter elementExporter = ElementExporters.getForType(element.getType()).orElseThrow(() -> new ExportException(String.format(
                            "Elements of type '%s' cannot be exported as there is no suitable exporter",
                            element.getType().name()
                    )));

                    elementExporter.afterExport(element, ctx);
                }
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
        TypeSetter typeSetter = createTypeSetter(ctx, document.getInfo().getLanguage());

        return typeSetter.typeset(document);
    }

    /**
     * Create a type setter.
     *
     * @param ctx      the export context
     * @param language to use
     * @return type setter to use
     */
    private TypeSetter createTypeSetter(ExportContext ctx, Language language) throws TypeSettingException {
        // Load hyphenation dictionary
        HyphenationDictionary hyphenationDictionary = HyphenationDictionaries.getDictionary(language).orElseThrow(() -> new TypeSettingException(String.format(
                "Could not find the hyphenation dictionary for language '%s'",
                language.name()
        )));

        // Load properties to use during typesetting
        Properties properties = new Properties();
        String path = String.format("/i18n/%s.properties", language.getCode());
        InputStream stream = KnuthPlassTypeSetter.class.getResourceAsStream(path);
        try (BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
            properties.load(br);
        } catch (IOException e) {
            throw new TypeSettingException(String.format(
                    "Could not load typesetting properties (containing translations needed by the process) for language '%s'",
                    language.name()
            ), e);
        }

        return new KnuthPlassTypeSetter(KnuthPlassTypeSettingConfig.newBuilder()
                .setMathFont(ctx.getMathFont())
                .setWorkingDirectory(ThawContext.getInstance().getCurrentFolder())
                .setTextParser(ThawContext.getInstance().getTextParser())
                .setProperties(properties)
                .setPageSize(ctx.getPageSize())
                .setPageInsets(ctx.getPageInsets())
                .setLooseness(1)
                .setFontDetailsSupplier(new FontDetailsSupplier() {
                    @Override
                    public StringMetrics measureString(DocumentNode node, int charBefore, String str) throws Exception {
                        double fontSize = ctx.getFontSizeForNode(node);
                        ThawFont font = ctx.getFontForNode(node);

                        KernedSize size = font.getKernedStringSize(charBefore, str, fontSize);

                        return new StringMetrics(size.getWidth(), size.getHeight(), size.getKerningAdjustments(), fontSize, font.getAscent(fontSize));
                    }

                    @Override
                    public double getSpaceWidth(DocumentNode node) throws Exception {
                        return ctx.getFontForNode(node).getCharacterSize(' ', ctx.getFontSizeForNode(node)).getWidth();
                    }
                })
                .setGlueConfig(new GlueConfig() {
                    @Override
                    public double getInterWordStretchability(DocumentNode node, char lastChar) throws Exception {
                        return ctx.getFontForNode(node).getCharacterSize(' ', ctx.getFontSizeForNode(node)).getWidth() / 2;
                    }

                    @Override
                    public double getInterWordShrinkability(DocumentNode node, char lastChar) throws Exception {
                        return ctx.getFontForNode(node).getCharacterSize(' ', ctx.getFontSizeForNode(node)).getWidth() * 0.7;
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
                .setImageSourceSupplier(src -> {
                    File currentProcessingFolder = ThawContext.getInstance().getCurrentFolder();
                    File imgFile = new File(currentProcessingFolder, src);

                    if (imgFile.getName().endsWith(".pdf")) {
                        PDFormXObject form;
                        Size size;
                        try(PDDocument srcDoc = PDDocument.load(imgFile)) {
                            PDPage srcPage = srcDoc.getPage(0);

                            LayerUtility layerUtility = new LayerUtility(ctx.getPdDocument());
                            form = layerUtility.importPageAsForm(srcDoc, srcPage);
                            size = new Size(srcPage.getMediaBox().getWidth(), srcPage.getMediaBox().getHeight());
                        }

                        return new PdfImageSource(
                                form,
                                size,
                                Unit.POINTS
                        );
                    } else {
                        PDImageXObject imageXObject = PDImageXObject.createFromFile(imgFile.getAbsolutePath(), ctx.getPdDocument());

                        return new PdfImageSource(
                                imageXObject,
                                new Size(imageXObject.getWidth(), imageXObject.getHeight()),
                                Unit.PIXEL
                        );
                    }
                })
                .build());
    }

}
