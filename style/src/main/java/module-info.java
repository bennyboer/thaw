module de.be.thaw.style {
    exports de.be.thaw.style.model;
    exports de.be.thaw.style.model.style;
    exports de.be.thaw.style.model.block;
    exports de.be.thaw.style.parser;
    exports de.be.thaw.style.parser.impl;
    exports de.be.thaw.style.parser.exception;
    exports de.be.thaw.style.parser.value;
    exports de.be.thaw.style.parser.value.exception;
    exports de.be.thaw.style.parser.value.impl;
    exports de.be.thaw.style.model.impl;
    exports de.be.thaw.style.model.selector;
    exports de.be.thaw.style.model.selector.builder;
    exports de.be.thaw.style.model.style.value;
    requires com.fasterxml.jackson.databind;
    requires org.jetbrains.annotations;
    requires de.be.thaw.font;
    requires de.be.thaw.util;

}
