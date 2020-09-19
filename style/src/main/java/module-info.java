module de.be.thaw.style {
    exports de.be.thaw.style.model;
    exports de.be.thaw.style.model.style;
    exports de.be.thaw.style.model.block;
    exports de.be.thaw.style.model.style.impl;
    exports de.be.thaw.style.parser;
    exports de.be.thaw.style.parser.impl;
    exports de.be.thaw.style.parser.exception;
    exports de.be.thaw.style.model.impl;
    requires com.fasterxml.jackson.databind;
    requires org.jetbrains.annotations;
    requires de.be.thaw.font;
    requires de.be.thaw.util;

}
