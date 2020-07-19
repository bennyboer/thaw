package de.be.thaw.shared;

import de.be.thaw.info.parser.InfoParser;
import de.be.thaw.reference.citation.source.model.parser.SourceParser;
import de.be.thaw.style.parser.StyleParser;
import de.be.thaw.text.parser.TextParser;

import java.io.File;
import java.nio.charset.Charset;

/**
 * Context of the currently running application.
 */
public class ThawContext {

    /**
     * Get the root folder of the Thaw project.
     */
    private File rootFolder;

    /**
     * The currently processing folder.
     */
    private File currentFolder;

    /**
     * The info parser to use.
     */
    private InfoParser infoParser;

    /**
     * The text parser to use.
     */
    private TextParser textParser;

    /**
     * Style parser to use.
     */
    private StyleParser styleParser;

    /**
     * Style parser to use.
     */
    private SourceParser sourceParser;

    /**
     * The encoding of the current Thaw project.
     */
    private Charset encoding;

    /**
     * Get the current instance of the thaw context.
     *
     * @return instance
     */
    public static ThawContext getInstance() {
        return InstanceHolder.INSTANCE;
    }

    private ThawContext() {
    }

    /**
     * Get the root folder of the Thaw project.
     *
     * @return root folder
     */
    public File getRootFolder() {
        return rootFolder;
    }

    /**
     * Set the root folder of the Thaw project.
     *
     * @param rootFolder to set
     */
    public void setRootFolder(File rootFolder) {
        this.rootFolder = rootFolder;
    }

    /**
     * Get the currently processing folder.
     *
     * @return currently processing folder
     */
    public File getCurrentFolder() {
        return currentFolder;
    }

    /**
     * Set the currently processing folder.
     *
     * @param currentFolder to set
     */
    public void setCurrentFolder(File currentFolder) {
        this.currentFolder = currentFolder;
    }

    /**
     * Get the info parser to use.
     *
     * @return info parser
     */
    public InfoParser getInfoParser() {
        return infoParser;
    }

    /**
     * Set the info parser to use.
     *
     * @param infoParser to set
     */
    public void setInfoParser(InfoParser infoParser) {
        this.infoParser = infoParser;
    }

    /**
     * Get the text parser to use.
     *
     * @return text parser
     */
    public TextParser getTextParser() {
        return textParser;
    }

    /**
     * Set the text parser to use.
     *
     * @param textParser to set
     */
    public void setTextParser(TextParser textParser) {
        this.textParser = textParser;
    }

    /**
     * Get the style parser to use.
     *
     * @return style parser
     */
    public StyleParser getStyleParser() {
        return styleParser;
    }

    /**
     * Set the style parser to use.
     *
     * @param styleParser to set
     */
    public void setStyleParser(StyleParser styleParser) {
        this.styleParser = styleParser;
    }

    /**
     * Get the source parser to use.
     *
     * @return source parser
     */
    public SourceParser getSourceParser() {
        return sourceParser;
    }

    /**
     * Set the source parser to use.
     *
     * @param sourceParser to set
     */
    public void setSourceParser(SourceParser sourceParser) {
        this.sourceParser = sourceParser;
    }

    /**
     * Get the current Thaw project encoding.
     *
     * @return encoding
     */
    public Charset getEncoding() {
        return encoding;
    }

    /**
     * Set the current Thaw project encoding.
     *
     * @param encoding to set
     */
    public void setEncoding(Charset encoding) {
        this.encoding = encoding;
    }

    /**
     * Holder of the font manager singleton instance.
     */
    private static final class InstanceHolder {

        /**
         * Instance of the thaw context.
         */
        static final ThawContext INSTANCE = new ThawContext();

    }

}
