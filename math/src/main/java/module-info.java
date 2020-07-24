module de.be.thaw.math {
    exports de.be.thaw.math.mathml.parser.impl;
    exports de.be.thaw.math.mathml.parser;
    exports de.be.thaw.math.mathml.parser.exception;
    exports de.be.thaw.math.mathml.tree;
    exports de.be.thaw.math.mathml.typeset;
    exports de.be.thaw.math.mathml.typeset.impl;
    exports de.be.thaw.math.mathml.typeset.exception;
    exports de.be.thaw.math.mathml.typeset.element;
    exports de.be.thaw.math.mathml.typeset.config;
    exports de.be.thaw.math.mathml.typeset.element.impl;

    requires java.xml;
    requires de.be.thaw.util;
    requires de.be.thaw.font;
    requires org.jetbrains.annotations;
}
