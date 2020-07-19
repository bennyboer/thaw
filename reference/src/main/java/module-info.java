module de.be.thaw.reference {
    requires org.jetbrains.annotations;
    requires com.fasterxml.jackson.databind;

    exports de.be.thaw.reference;
    exports de.be.thaw.reference.impl;
    exports de.be.thaw.reference.citation;
    exports de.be.thaw.reference.citation.source.model.parser;
    exports de.be.thaw.reference.citation.source.model.parser.impl;
    exports de.be.thaw.reference.citation.source.model.parser.exception;
    exports de.be.thaw.reference.citation.source.model;
    exports de.be.thaw.reference.citation.styles;
    exports de.be.thaw.reference.citation.source;
    exports de.be.thaw.reference.citation.styles.exception;
    exports de.be.thaw.reference.citation.styles.referencelist;

}
