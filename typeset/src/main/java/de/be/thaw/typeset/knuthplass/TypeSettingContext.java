package de.be.thaw.typeset.knuthplass;

import de.be.thaw.typeset.knuthplass.config.KnuthPlassTypeSettingConfig;
import de.be.thaw.typeset.knuthplass.paragraph.Paragraph;
import de.be.thaw.typeset.page.Element;
import de.be.thaw.typeset.page.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Context used during typesetting.
 */
public class TypeSettingContext {

    /**
     * Configuration of the Knuth-Plass type setting algorithm.
     */
    private final KnuthPlassTypeSettingConfig config;

    /**
     * List of lists of consecutive paragraphs to type set.
     */
    private final List<List<Paragraph>> consecutiveParagraphLists;

    /**
     * The currently typeset pages.
     */
    private List<Page> pages = new ArrayList<>();

    /**
     * Elements of the current page.
     */
    private List<Element> currentPageElements = new ArrayList<>();

    /**
     * The current floating configuration.
     */
    private final FloatConfig floatConfig = new FloatConfig();

    /**
     * Current position context of where we are currently typesetting at.
     */
    private final PositionContext positionContext = new PositionContext();

    public TypeSettingContext(KnuthPlassTypeSettingConfig config, List<List<Paragraph>> consecutiveParagraphLists) {
        this.config = config;
        this.consecutiveParagraphLists = consecutiveParagraphLists;

        positionContext.setY(config.getPageInsets().getTop()); // Initialize y-offset
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
     * Push the current page elements to a new page.
     */
    public void pushPage() {
        pages.add(new Page(pages.size() + 1, config.getPageSize(), config.getPageInsets(), currentPageElements));

        currentPageElements = new ArrayList<>();
        getPositionContext().setY(config.getPageInsets().getTop());

        // Reset the floating configuration
        getFloatConfig().reset();
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
