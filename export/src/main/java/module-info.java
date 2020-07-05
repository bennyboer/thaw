module de.be.thaw.export {
    requires org.apache.pdfbox;
    requires org.apache.fontbox;
    requires java.logging;

    requires de.be.thaw.typeset;
    requires de.be.thaw.core;
    requires de.be.thaw.text;
    requires de.be.thaw.font;
    requires de.be.thaw.info;
    requires de.be.thaw.hyphenation;

    exports de.be.thaw.export;
    exports de.be.thaw.export.pdf;
    exports de.be.thaw.export.exception;
}
