module de.be.thaw.core {
    requires de.be.thaw.text;
    requires de.be.thaw.info;
    requires de.be.thaw.style;
    requires de.be.thaw.font;
    requires de.be.thaw.reference;
    requires de.be.thaw.shared;
    requires de.be.thaw.util;
    requires org.jetbrains.annotations;

    exports de.be.thaw.core.document;
    exports de.be.thaw.core.document.util;
    exports de.be.thaw.core.document.node;
    exports de.be.thaw.core.document.node.style;
    exports de.be.thaw.core.document.convert;
    exports de.be.thaw.core.document.convert.exception;
    exports de.be.thaw.core.document.builder.impl;
    exports de.be.thaw.core.document.builder.impl.exception;
    exports de.be.thaw.core.document.builder.impl.source;
}
