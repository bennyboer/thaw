module de.be.thaw.font {
    exports de.be.thaw.font.util;
    exports de.be.thaw.font.util.file;
    exports de.be.thaw.font;
    exports de.be.thaw.font.util.exception;
    exports de.be.thaw.font.opentype.gpos;

    requires java.desktop;
    requires de.be.thaw.util;
    requires org.jetbrains.annotations;
    requires java.logging;
}
