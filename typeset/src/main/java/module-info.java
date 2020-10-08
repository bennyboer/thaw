module de.be.thaw.typeset {
    exports de.be.thaw.typeset;
    exports de.be.thaw.typeset.util;
    exports de.be.thaw.typeset.page;
    exports de.be.thaw.typeset.exception;
    exports de.be.thaw.typeset.kerning;
    exports de.be.thaw.typeset.kerning.glyph;
    exports de.be.thaw.typeset.kerning.optical;
    exports de.be.thaw.typeset.knuthplass;
    exports de.be.thaw.typeset.knuthplass.config;
    exports de.be.thaw.typeset.knuthplass.config.util;
    exports de.be.thaw.typeset.knuthplass.config.util.hyphen;
    exports de.be.thaw.typeset.knuthplass.config.util.image;
    exports de.be.thaw.typeset.knuthplass.util;
    exports de.be.thaw.typeset.knuthplass.paragraph;
    exports de.be.thaw.typeset.knuthplass.paragraph.impl;
    exports de.be.thaw.typeset.knuthplass.exception;
    exports de.be.thaw.typeset.page.impl;
    exports de.be.thaw.typeset.page.util;

    requires de.be.thaw.core;
    requires de.be.thaw.text;
    requires de.be.thaw.info;
    requires de.be.thaw.style;
    requires de.be.thaw.reference;
    requires de.be.thaw.font;
    requires de.be.thaw.util;
    requires de.be.thaw.math;
    requires de.be.thaw.code;
    requires de.be.thaw.table;
    requires org.jetbrains.annotations;
    requires java.desktop;
    requires rtfparserkit;
}
