module de.be.thaw.export {
    requires org.apache.pdfbox;
    requires org.apache.fontbox;
    requires java.logging;
    requires org.jetbrains.annotations;

    requires de.be.thaw.typeset;
    requires de.be.thaw.core;
    requires de.be.thaw.text;
    requires de.be.thaw.font;
    requires de.be.thaw.info;
    requires de.be.thaw.hyphenation;
    requires de.be.thaw.style;
    requires de.be.thaw.reference;
    requires de.be.thaw.util;
    requires de.be.thaw.math;
    requires de.be.thaw.shared;

    exports de.be.thaw.export;
    exports de.be.thaw.export.pdf;
    exports de.be.thaw.export.exception;
    exports de.be.thaw.export.pdf.font;
}
