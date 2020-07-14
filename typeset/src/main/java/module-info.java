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

    requires de.be.thaw.core;
    requires de.be.thaw.text;
    requires de.be.thaw.info;
    requires java.desktop;
    requires de.be.thaw.style;
    requires de.be.thaw.reference;
}
