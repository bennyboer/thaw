package de.be.thaw.typeset.knuthplass;

import de.be.thaw.core.document.Document;
import de.be.thaw.core.document.builder.impl.DefaultDocumentBuilder;
import de.be.thaw.core.document.builder.impl.exception.DocumentBuildException;
import de.be.thaw.core.document.builder.impl.source.DocumentBuildSource;
import de.be.thaw.core.document.node.DocumentNode;
import de.be.thaw.core.document.util.PageRange;
import de.be.thaw.style.model.StyleModel;
import de.be.thaw.style.model.block.StyleBlock;
import de.be.thaw.style.model.style.Style;
import de.be.thaw.style.model.style.StyleType;
import de.be.thaw.style.model.style.impl.HeaderFooterStyle;
import de.be.thaw.style.model.style.impl.InsetsStyle;
import de.be.thaw.style.model.style.impl.SizeStyle;
import de.be.thaw.text.model.TextModel;
import de.be.thaw.text.parser.exception.ParseException;
import de.be.thaw.typeset.exception.TypeSettingException;
import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.knuthplass.util.RethrowingBiFunction;
import de.be.thaw.typeset.page.AbstractElement;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;
import de.be.thaw.typeset.util.Insets;
import de.be.thaw.util.Position;
import de.be.thaw.util.Size;
import org.jetbrains.annotations.Nullable;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Context used during typesetting.
 */
public class TypeSettingContext {

    /**
     * Inches per printer point conversion factor.
     */
    private static final double INCHES_PER_POINT = 1.0 / 72;

    /**
     * Millimeters per inch conversion factor.
     */
    private static final double MM_PER_INCH = 25.4;

    /**
     * Configuration of the Knuth-Plass type setting algorithm.
     */
    private final KnuthPlassTypeSettingConfig config;

    /**
     * The currently typeset pages.
     */
    private final List<Page> pages = new ArrayList<>();

    /**
     * Elements of the current page.
     */
    private List<Element> currentPageElements = new ArrayList<>();

    /**
     * Current foot note page elements.
     */
    private List<List<Element>> currentFootNotePageElements = new ArrayList<>();

    /**
     * Current height used by foot note elements.
     */
    private double currentFootNoteElementsHeight = 0;

    /**
     * The current floating configuration.
     */
    private final FloatConfig floatConfig = new FloatConfig();

    /**
     * Current position context of where we are currently typesetting at.
     */
    private final PositionContext positionContext = new PositionContext();

    /**
     * The model needed by the typesetter.
     */
    private final List<List<Paragraph>> consecutiveParagraphLists;

    /**
     * All available header paragraph lists.
     */
    private final Map<PageRange, List<List<Paragraph>>> headerParagraphs;

    /**
     * All available footer paragraph lists.
     */
    private final Map<PageRange, List<List<Paragraph>>> footerParagraphs;

    /**
     * Function used to typeset a passed list of paragraphs to pages.
     * Used to typeset headers and footer paragraph lists on demand.
     */
    @Nullable
    private final RethrowingBiFunction<List<List<Paragraph>>, TypeSettingContext, List<Page>, TypeSettingException> typesettingFunction;

    /**
     * Function used to typeset a document.
     */
    @Nullable
    private final RethrowingBiFunction<Document, KnuthPlassTypeSettingConfig, List<Page>, TypeSettingException> typesetDocumentFunction;

    /**
     * Offset added to the current page number.
     * Used for example when typesetting headers and footers on demand.
     */
    private int pageNumberOffset = 0;

    /**
     * The source Thaw document.
     */
    private final Document document;

    /**
     * Foot note paragraphs in the document to typeset.
     */
    private final Map<String, List<List<Paragraph>>> footNoteParagraphs;

    /**
     * Current foot note index.
     */
    private int footNoteNumber = 1;

    /**
     * Counter for line numbers.
     */
    private int lineNumberCounter = 0;

    public TypeSettingContext(
            KnuthPlassTypeSettingConfig config,
            Document document,
            List<List<Paragraph>> consecutiveParagraphLists,
            @Nullable Map<PageRange, List<List<Paragraph>>> headerParagraphs,
            @Nullable Map<PageRange, List<List<Paragraph>>> footerParagraphs,
            @Nullable Map<String, List<List<Paragraph>>> footNoteParagraphs,
            @Nullable RethrowingBiFunction<List<List<Paragraph>>, TypeSettingContext, List<Page>, TypeSettingException> typesettingFunction,
            @Nullable RethrowingBiFunction<Document, KnuthPlassTypeSettingConfig, List<Page>, TypeSettingException> typesetDocumentFunction
    ) {
        this.config = config;
        this.pageNumberOffset = config.getPageNumberOffset();

        this.document = document;
        this.consecutiveParagraphLists = consecutiveParagraphLists;

        this.headerParagraphs = headerParagraphs;
        this.footerParagraphs = footerParagraphs;
        this.footNoteParagraphs = footNoteParagraphs;
        this.typesettingFunction = typesettingFunction;
        this.typesetDocumentFunction = typesetDocumentFunction;

        positionContext.setY(config.getPageInsets().getTop()); // Initialize y-offset
    }

    /**
     * Get available height on the page.
     *
     * @return available height
     */
    public double getAvailableHeight() {
        return (getConfig().getPageSize().getHeight() - getConfig().getPageInsets().getBottom()) - getPositionContext().getY() - getCurrentFootNoteElementsHeight();
    }

    /**
     * Current height taken up by foot notes.
     *
     * @return current height taken up by foot notes
     */
    public double getCurrentFootNoteElementsHeight() {
        return currentFootNoteElementsHeight;
    }

    public Document getDocument() {
        return document;
    }

    public KnuthPlassTypeSettingConfig getConfig() {
        return config;
    }

    public List<List<Paragraph>> getConsecutiveParagraphLists() {
        return consecutiveParagraphLists;
    }

    public List<Page> getPages() {
        return pages;
    }

    public List<Element> getCurrentPageElements() {
        return currentPageElements;
    }

    public FloatConfig getFloatConfig() {
        return floatConfig;
    }

    public PositionContext getPositionContext() {
        return positionContext;
    }

    /**
     * Get the offset added to the page number.
     *
     * @return page number offset
     */
    public int getPageNumberOffset() {
        return pageNumberOffset;
    }

    /**
     * Set a offset that is added to the page number.
     *
     * @param pageNumberOffset to set
     */
    public void setPageNumberOffset(int pageNumberOffset) {
        this.pageNumberOffset = pageNumberOffset;
    }

    /**
     * Push the current page elements to a new page.
     */
    public void pushPage() throws TypeSettingException {
        addHeaderFooterToPage();
        addFootNotesToPage();

        pages.add(new Page(getCurrentPageNumber(), config.getPageSize(), config.getPageInsets(), currentPageElements));

        currentPageElements = new ArrayList<>();
        getPositionContext().setY(config.getPageInsets().getTop());

        // Reset the floating configuration
        getFloatConfig().reset();
    }

    /**
     * Add all pending foot notes to the page.
     */
    private void addFootNotesToPage() {
        double startY = config.getPageSize().getHeight() - config.getPageInsets().getBottom() - currentFootNoteElementsHeight;

        for (List<Element> elements : currentFootNotePageElements) {
            // Position the elements properly
            double maxY = 0;
            for (Element element : elements) {
                AbstractElement e = (AbstractElement) element;
                e.setPosition(new Position(
                        element.getPosition().getX(),
                        element.getPosition().getY() - config.getPageInsets().getTop() + startY
                ));

                if (element.getPosition().getY() + element.getSize().getHeight() > maxY) {
                    maxY = element.getPosition().getY() + element.getSize().getHeight();
                }
            }

            currentPageElements.addAll(elements);

            startY = maxY;
        }

        currentFootNotePageElements.clear();
        currentFootNoteElementsHeight = 0;
    }

    /**
     * Add a header and footer (if any) to the current page elements.
     */
    private void addHeaderFooterToPage() throws TypeSettingException {
        if (typesettingFunction == null) {
            return;
        }

        int currentPageNumber = getCurrentPageNumber();

        List<List<Paragraph>> headerParagraphList = getHeaderFooterForPageNumber(currentPageNumber, headerParagraphs);
        List<List<Paragraph>> footerParagraphList = getHeaderFooterForPageNumber(currentPageNumber, footerParagraphs);

        if (headerParagraphList != null) {
            TypeSettingContext newContext = new TypeSettingContext(config, document, headerParagraphList, null, null, null, null, null);
            newContext.setPageNumberOffset(currentPageNumber - 1);

            List<Page> headerPages = typesettingFunction.apply(headerParagraphList, newContext);
            Page headerPage = headerPages.get(0); // We are only interested in the first page -> Content should actually be only a portion of one page

            double pageXInsets = headerPage.getInsets().getLeft();
            double pageYInsets = headerPage.getInsets().getTop();

            headerPage.getElements().sort(Comparator.comparingDouble(e -> e.getPosition().getY() + e.getSize().getHeight()));
            Element lastElm = headerPage.getElements().get(headerPage.getElements().size() - 1);
            double maxY = lastElm.getPosition().getY() + lastElm.getSize().getHeight();

            // Position elements properly on the actual page
            for (Element element : headerPage.getElements()) {
                AbstractElement e = (AbstractElement) element;

                e.setPosition(new Position(
                        element.getPosition().getX() - pageXInsets + config.getPageInsets().getLeft(),
                        element.getPosition().getY() - maxY + pageYInsets
                ));

                currentPageElements.add(e);
            }
        }

        if (footerParagraphList != null) {
            TypeSettingContext newContext = new TypeSettingContext(config, document, footerParagraphList, null, null, null, null, null);
            newContext.setPageNumberOffset(currentPageNumber - 1);

            List<Page> footerPages = typesettingFunction.apply(footerParagraphList, newContext);
            Page footerPage = footerPages.get(0); // We are only interested in the first page -> Content should actually be only a portion of one page

            double pageXInsets = footerPage.getInsets().getLeft();
            double pageYInsets = footerPage.getInsets().getTop();

            double startY = config.getPageSize().getHeight() - config.getPageInsets().getBottom();

            // Position elements properly on the actual page
            for (Element element : footerPage.getElements()) {
                AbstractElement e = (AbstractElement) element;

                e.setPosition(new Position(
                        element.getPosition().getX() - pageXInsets + config.getPageInsets().getLeft(),
                        element.getPosition().getY() - pageYInsets + startY
                ));

                currentPageElements.add(e);
            }
        }
    }

    /**
     * Get the header or footer paragraph list for the passed page number from the given lookup.
     *
     * @param pageNumber         to get header or footer paragraph list for
     * @param headerFooterLookup to get list from
     * @return the paragraph list
     */
    private List<List<Paragraph>> getHeaderFooterForPageNumber(int pageNumber, Map<PageRange, List<List<Paragraph>>> headerFooterLookup) {
        if (headerFooterLookup == null) {
            return null;
        }

        for (Map.Entry<PageRange, List<List<Paragraph>>> entry : headerFooterLookup.entrySet()) {
            PageRange range = entry.getKey();
            if (range == null) {
                continue; // Skip the default header or footer entry
            }

            int startPage = range.getStartPage().orElse(1);

            if (startPage <= pageNumber && (range.getEndPage().isEmpty() || range.getEndPage().get() >= pageNumber)) {
                return entry.getValue();
            }
        }

        return headerFooterLookup.get(null); // Return the default header or footer
    }

    /**
     * Push the passed page element to the current page.
     *
     * @param element to push
     */
    public void pushPageElement(Element element) {
        currentPageElements.add(element);
    }

    /**
     * Get the current page number.
     *
     * @return current page number
     */
    public int getCurrentPageNumber() {
        return pages.size() + 1 + getPageNumberOffset();
    }

    /**
     * Push a foot note to the current page.
     */
    public void pushFootNote(DocumentNode node) throws TypeSettingException {
        List<List<Paragraph>> paragraphLists = footNoteParagraphs.get(node.getId());
        List<Page> pages = typesettingFunction.apply(paragraphLists, new TypeSettingContext(config, document, paragraphLists, null, null, null, null, null));
        Page page = pages.get(0);

        page.getElements().sort(Comparator.comparingDouble(e -> e.getPosition().getY() + e.getSize().getHeight()));
        Element lastElement = page.getElements().get(page.getElements().size() - 1);
        double height = lastElement.getPosition().getY() + lastElement.getSize().getHeight() - page.getInsets().getTop();

        // TODO Add foot note number as element
        currentFootNotePageElements.add(page.getElements());
        currentFootNoteElementsHeight += height;

        footNoteNumber++;
    }

    /**
     * Push a foot note by its elements.
     *
     * @param elements to push
     */
    public void pushFootNote(List<Element> elements) {
        Element lastElement = elements.get(elements.size() - 1);
        double height = lastElement.getPosition().getY() + lastElement.getSize().getHeight() - config.getPageInsets().getTop();
        currentFootNoteElementsHeight += height;
        currentFootNotePageElements.add(elements);
    }

    /**
     * Pop the last foot notes elements.
     *
     * @return last foot notes elements
     */
    public List<Element> popFootNote() {
        if (currentFootNotePageElements.size() > 0) {
            List<Element> elements = currentFootNotePageElements.get(currentFootNotePageElements.size() - 1);
            Element lastElement = elements.get(elements.size() - 1);
            double height = lastElement.getPosition().getY() + lastElement.getSize().getHeight() - config.getPageInsets().getTop();

            currentFootNoteElementsHeight -= height;

            return currentFootNotePageElements.remove(currentFootNotePageElements.size() - 1);
        }

        return null;
    }

    /**
     * Increase and get the current line number.
     */
    public int increaseAndGetLineNumber() {
        return ++lineNumberCounter;
    }

    /**
     * Typeset the passed document.
     *
     * @param document      to typeset
     * @param configuration to use
     * @return the typeset pages
     * @throws TypeSettingException in case the passed document could not be typeset
     */
    private List<Page> typesetDocument(Document document, KnuthPlassTypeSettingConfig configuration) throws TypeSettingException {
        if (typesetDocumentFunction == null) {
            throw new TypeSettingException("Cannot typeset the passed document as the current typesetting context is not suitable for this kind of action!");
        }

        return typesetDocumentFunction.apply(document, configuration);
    }

    /**
     * Typeset the passed string written in Thaw document text format (*.tdt).
     *
     * @param text             to typeset
     * @param width            to use for typesetting (in printer points)
     * @param customStyleModel a custom style model to use
     * @return the typeset pages
     * @throws TypeSettingException in case the passed text could not be typset to pages properly
     */
    public List<Page> typesetThawTextFormat(String text, double width, @Nullable StyleModel customStyleModel) throws TypeSettingException {
        // Parse Thaw document text format.
        TextModel textModel;
        try {
            textModel = getConfig().getTextParser().parse(new StringReader(text));
        } catch (ParseException e) {
            throw new TypeSettingException(String.format(
                    "Could not parse the following string written in the Thaw document text format: '%s'. Exception message was: %s",
                    text,
                    e.getMessage()
            ), e);
        }

        // Create style model to use.
        StyleModel styleModel = new StyleModel(new HashMap<>()).merge(getDocument().getStyleModel());

        Map<StyleType, Style> styles = new HashMap<>();
        double mmWidth = width * INCHES_PER_POINT * MM_PER_INCH;
        styles.put(StyleType.SIZE, new SizeStyle(mmWidth, Double.MAX_VALUE));
        styles.put(StyleType.INSETS, new InsetsStyle(0.0, 0.0, 0.0, 0.0));
        styles.put(StyleType.HEADER_FOOTER, new HeaderFooterStyle(null, null));
        StyleBlock newDocumentBlock = new StyleBlock("DOCUMENT", styles);

        styleModel.addBlock(newDocumentBlock.getName(), newDocumentBlock.merge(styleModel.getBlock(newDocumentBlock.getName()).orElseThrow()));

        if (customStyleModel != null) {
            styleModel = customStyleModel.merge(styleModel);
        }

        // Create Thaw document.
        Document newDocument;
        try {
            newDocument = new DefaultDocumentBuilder().build(new DocumentBuildSource(
                    getDocument().getInfo(),
                    textModel,
                    styleModel,
                    getDocument().getSourceModel(),
                    getDocument().getReferenceModel(),
                    getDocument()
            ));
        } catch (DocumentBuildException e) {
            throw new TypeSettingException(String.format(
                    "Could not build a document for the following string written in the Thaw document text format: '%s'. Exception message was: %s",
                    text,
                    e.getMessage()
            ), e);
        }

        // Typeset the document.
        return typesetDocument(newDocument, KnuthPlassTypeSettingConfig.newBuilder(getConfig())
                .setPageSize(new Size(width, Double.MAX_VALUE))
                .setPageInsets(new Insets(0))
                .setPageNumberOffset(getCurrentPageNumber() - 1)
                .build());
    }

    /**
     * Float configuration in case we deal with floating paragraphs.
     */
    public static class FloatConfig {

        /**
         * Floating until this Y-offset.
         */
        private double floatUntilY = -1;

        /**
         * Indent that applies to other paragraphs due to a floating paragraph.
         */
        private double floatIndent = 0;

        /**
         * Width of the floating element nearby.
         */
        private double floatWidth = 0;

        /**
         * Reset the floating configuration to the default values.
         */
        public void reset() {
            floatUntilY = -1;
            floatIndent = 0;
            floatWidth = 0;
        }

        public double getFloatUntilY() {
            return floatUntilY;
        }

        public void setFloatUntilY(double floatUntilY) {
            this.floatUntilY = floatUntilY;
        }

        public double getFloatIndent() {
            return floatIndent;
        }

        public void setFloatIndent(double floatIndent) {
            this.floatIndent = floatIndent;
        }

        public double getFloatWidth() {
            return floatWidth;
        }

        public void setFloatWidth(double floatWidth) {
            this.floatWidth = floatWidth;
        }

    }

    /**
     * Position we are currently typesetting at.
     */
    public static class PositionContext {

        /**
         * Y-offset.
         */
        private double y = 0;

        /**
         * X-offset.
         */
        private double x = 0;

        public double getY() {
            return y;
        }

        public void setY(double y) {
            this.y = y;
        }

        public void increaseY(double value) {
            y += value;
        }

        public double getX() {
            return x;
        }

        public void setX(double x) {
            this.x = x;
        }

        public void increaseX(double value) {
            x += value;
        }

    }

}
