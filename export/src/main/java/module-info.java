module de.be.thaw.export {
    requires org.apache.pdfbox;
    requires de.be.thaw.typeset;
    requires de.be.thaw.core;
    requires de.be.thaw.text;

    exports de.be.thaw.export;
    exports de.be.thaw.export.pdf;
    exports de.be.thaw.export.exception;
}
