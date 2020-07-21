package de.be.thaw.math.mathml.parser.impl.handler;

/**
 * Abstract parse handler.
 */
public abstract class AbstractMathMLNodeParseHandler implements MathMLNodeParseHandler {

    /**
     * Name of the node this handler is able to parse.
     */
    private final String nodeName;

    public AbstractMathMLNodeParseHandler(String nodeName) {
        this.nodeName = nodeName;
    }

    @Override
    public String getNodeName() {
        return nodeName;
    }

}
