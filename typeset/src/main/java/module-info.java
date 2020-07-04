module de.be.thaw.typeset {
    exports de.be.thaw.typeset;
    exports de.be.thaw.typeset.util;
    exports de.be.thaw.typeset.page;
    exports de.be.thaw.typeset.exception;
    exports de.be.thaw.typeset.knuthplass;
    exports de.be.thaw.typeset.knuthplass.config;
    exports de.be.thaw.typeset.knuthplass.config.util;
    exports de.be.thaw.typeset.knuthplass.config.util.hyphen;
    exports de.be.thaw.typeset.page.impl;

    requires de.be.thaw.core;
    requires de.be.thaw.text;
    requires java.desktop;
}
